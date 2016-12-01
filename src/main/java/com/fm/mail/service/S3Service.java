package com.fm.mail.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.fm.mail.dto.EMail;
import org.apache.james.mime4j.message.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by thihara on 10/25/16.
 */
@Service
public class S3Service {
    private BasicAWSCredentials awsCreds;
    private AmazonS3 s3Client;
    private static final String BUCKET_NAME = "";

    public S3Service() {
        awsCreds = new BasicAWSCredentials("", "");
        s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .withRegion("us-west-2")
                .build();
    }

    public List<S3ObjectSummary> listAllFiles(String prefix) {
        ListObjectsV2Result result = s3Client.listObjectsV2(BUCKET_NAME, prefix + "/");

        return result.getObjectSummaries();
    }

    public EMail eMailContent(String fileKey) {

        try(S3Object object = s3Client.getObject(BUCKET_NAME, fileKey)){
            EMail eMail = new EMail();

            StringBuilder txtBody = new StringBuilder();
            StringBuilder htmlBody = new StringBuilder();

            Message mimeMsg = new Message(object.getObjectContent());

            if (mimeMsg.isMultipart()) {
                parseBodyParts((Multipart) mimeMsg.getBody(), txtBody, htmlBody);
            } else {
                txtBody.append(getTxtPart(mimeMsg));
            }

            eMail.setId(mimeMsg.getMessageId());
            eMail.setSubject(mimeMsg.getSubject());
            eMail.setFrom(mimeMsg.getFrom().toString());
            eMail.setTo(mimeMsg.getTo().stream()
                    .map(address -> address.getDisplayString())
                    .collect(Collectors.<String>toList()));
            eMail.setTextContent(txtBody.toString());

            return eMail;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<EMail> emailContents(Collection<String> fileKeys){
        return fileKeys.stream()
                .map(fileKey -> eMailContent(fileKey))
                .collect(Collectors.toList());
    }

    private void parseBodyParts(Multipart multipart, StringBuilder txtBody, StringBuilder htmlBody) throws IOException {
        for (BodyPart part : multipart.getBodyParts()) {
            if (part.isMimeType("text/plain")) {
                String txt = getTxtPart(part);
                txtBody.append(txt);
            } else if (part.isMimeType("text/html")) {
                String html = getTxtPart(part);
                htmlBody.append(html);
            } else if (part.getDispositionType() != null && !part.getDispositionType().equals("")) {
                //If DispositionType is null or empty, it means that it's multipart, not attached file
                // Ignore for now, since this is a simple demo.
            }

            //If current part contains other, parse it again by recursion
            if (part.isMultipart()) {
                parseBodyParts((Multipart) part.getBody(), txtBody, htmlBody);
            }
        }
    }

    private String getTxtPart(Entity part) throws IOException {
        //Get content from body
        TextBody tb = (TextBody) part.getBody();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        tb.writeTo(baos);
        return new String(baos.toByteArray());
    }
}
