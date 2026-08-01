pipeline {
    agent any
    tools { jdk 'jdk-21'; maven 'maven-3.9' }
    options { timestamps(); disableConcurrentBuilds() }
    stages {
        stage('Build and test') {
            steps { sh 'mvn --batch-mode clean verify' }
            post { always { junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml' } }
        }
        stage('SonarQube') {
            when { changeRequest() }
            steps {
                withSonarQubeEnv('sonarqube') {
                    sh 'mvn --batch-mode sonar:sonar'
                }
            }
        }
        stage('Quality gate') {
            when { changeRequest() }
            steps { timeout(time: 10, unit: 'MINUTES') { waitForQualityGate abortPipeline: true } }
        }
        stage('Container validation') {
            steps { sh 'docker compose config --quiet' }
        }
        stage('Build containers') {
            when { branch 'main' }
            steps { sh 'docker compose build' }
        }
    }
}

