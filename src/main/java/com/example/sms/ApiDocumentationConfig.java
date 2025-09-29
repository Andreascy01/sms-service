package com.example.sms;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;

import io.quarkus.runtime.Application;

@OpenAPIDefinition(info = @Info(title = "SMS Service API", version = "1.0.0", description = "This API allows users to send, search, and manage SMS messages. "
        + "It supports filtering by status, source number, and destination number, "
        + "and simulates message delivery.", contact = @Contact(name = "Your Name", email = "your.email@example.com"), license = @License(name = "Apache 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0.html")))
public class ApiDocumentationConfig {

}
