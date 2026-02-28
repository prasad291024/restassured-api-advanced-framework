pipeline {
    agent any

    tools {
        jdk 'JDK22'
        maven 'Maven3'
    }

    parameters {
        choice(name: 'ENV', choices: ['dev', 'qa', 'prod'], description: 'Target environment')
        string(name: 'SUITE', defaultValue: 'testng.xml', description: 'TestNG suite XML file')
    }

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn -B -DskipTests clean compile'
            }
        }

        stage('Test') {
            steps {
                sh "mvn -B test -Denv=${params.ENV} -DsuiteXmlFile=${params.SUITE}"
            }
        }
    }

    post {
        always {
            junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: true
            archiveArtifacts artifacts: 'allure-results/**,target/allure-results/**,target/surefire-reports/**', allowEmptyArchive: true
        }
    }
}
