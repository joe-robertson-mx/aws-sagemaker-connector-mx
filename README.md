# aws-sagemaker-connector-mx
Development project for the AWS SageMaker Mendix connector

# Description
This module is an example for developing Mendix connectors to AWS services.
 
It has a single connector for invoking an AWS SageMaker endpoint running the JumpStart Question Answering Distilbert Base Uncased model within Amazon SageMaker.
 
# Typical usage scenario
To call the model ML model, and to form the basis for other AWS Connector modules.

# Features and Limitations
Currently only supports the single model type.

# Installation
First install the AWS Authentication Connector and then install this mpk.

# Configuration
- Setup your AWS SageMaker model and correct IAM roles
- Install the AWS Authentication Connector
- Call the Authentication connector and configure the QuestionAnswer microflow action

# Development
To contribute to this project fork the project and submit a pull request
