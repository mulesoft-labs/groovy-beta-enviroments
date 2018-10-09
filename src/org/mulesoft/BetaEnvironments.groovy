#!/usr/bin/env groovy
package org.mulesoft;

import groovy.json.*

/*
    Usage:

    def betaEnvs = new BetaEnvironments("test-env", "YOUR_API_KEY")
    betaEnvs.create_environment()
    def details = betaEnvs.get_environment_details()
    betaEnvs.delete_environment()
*/

class BetaEnvironments implements Serializable {
    static final long serialVersionUID = 3526473395612776159L
    String token
    String name
    String baseUrl
    Map details

    public BetaEnvironments (name, token) {
        this.name = name
        this.token = token
        this.baseUrl = "https://beta.anypoint-environments.mulesoft.com/api/v1"
    }

    // TODO: Test this works
    Map create_environment () {
        def request = new URL("${this.baseUrl}/environments").openConnection();
        def message = BetaEnvironments.get_post_message(this.name)
        request.setRequestMethod("POST")
        request.setDoOutput(true)
        request.setRequestProperty("Content-Type", "application/json")
        request.setRequestProperty("Authorization", "Bearer ${this.token}")
        request.getOutputStream().write(message.getBytes("UTF-8"));
        def response_code = request.getResponseCode();
        if(!response_code.equals(201)) {
            throw new Exception("Response code is " + response_code + " which doesn't match 201")
        }
        return new JsonSlurperClassic().parseText(request.getInputStream().getText())
    }

    static String get_post_message (name) {
        def opts = [
            "name"            : name,
            "type"            : "pce",
            "nodes"           : 3,
            "installer_url"   : "s3://onprem-standalone-installers/anypoint-2.0.0-installer.tar.gz",
            "provider"        : "aws",
            "pce_aws_account" : true,
            "aws_region"      : "us-east-2",
            "username"        : "username",
            "password"        : "Password1",
            "email"           : "username@mulesoft.com"
        ]
        return JsonOutput.toJson(opts)
    }

    // TODO: test
    Map get_environment_details () {
        def request = new URL("${this.baseUrl}/environments").openConnection();
        request.setRequestProperty("Authorization", "Bearer ${this.token}")
        def response_code = request.getResponseCode();
        if(!response_code.equals(200)) {
            throw new Exception("Response code is " + response_code + " which doesn't match 200")
        }
        def allEnvironments = new JsonSlurperClassic().parseText(request.getInputStream().getText())
        def environment
        for (def i = 0; i < allEnvironments.size(); i++) {
            if (allEnvironments[i].name == this.name) {
                environment = allEnvironments[i]
            }
        }
        if (environment == null) {
            throw new Exception("Requested environments not found (" + this.name + ")")
        }
        return environment
    }

    // TODO: See how it works
    void delete_environment () {
        this.details = this.get_environment_details()
        def request = new URL("${this.baseUrl}/environments/${this.details.id}").openConnection();
        request.setRequestMethod("DELETE")
        request.setRequestProperty("Authorization", "Bearer ${this.token}")
        def response_code = request.getResponseCode();
        if(!response_code.equals(504)) {
            throw new Exception("Response code is " + response_code + " which doesn't match 504. Environment not deleted properly.")
        }

    }

    public String toString() {
        def obj = [
            "name": this.name,
            "token": this.token,
            "details": this.details,
            "baseUrl": this.baseUrl
        ]
        return JsonOutput.toJson()
    }
}
