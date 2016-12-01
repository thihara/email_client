# email_client

This is a small SpringBoot application written in Java to test an email setup I've done with 
[Haraka](https://github.com/haraka/Haraka).

The relevant setup details can be found in my [blog post](http://thihara.github.io/Creating-E-Mail-Service-with-Haraka/).

# Dependencies

You need [maven](https://maven.apache.org/) and Java 8 installed to build and run this. 

Apart from that the project depends on the SpringBoot libraries and the following additional libraries.

* apache-mime4j
* aws-java-sdk
* apache commons-email

## Configurations

The application has all the configuration parameters hardcoded (meh, don't judge me, it's a small demo app).

You want to change the email service connection parameters from the `EMailService.java` file and the AWS S3 bucket connection
parameters from the `S3Service.java` file.

# Running

you can run the application by running `mvn clean install spring-boot:run` the first time and then just 
by `mvn spring-boot:run`.

# Commands

This application supports the following endpoints,

## /email HTTP GET

Connects to the AWS S3 bucket and lists all emails that belong the the given email address. The raw MIME email will be parsed.

Parameters supported are 
1. emailAddress - Email address, whose content is to be listed.

## /email HTTP POST

Sends an outgoing email with the given paramters.

Parameters supported are

1. from - Address of the sender
2. to - Address of the receiver
3. subject - Email subject
4. bodyContent - Email content (body)

You can use `curl` to test these out once the application is running.
