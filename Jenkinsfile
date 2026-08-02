pipeline {
    agent any
    options { timestamps(); disableConcurrentBuilds() }
    stages {
        stage('Build and test') {
            steps { sh 'mvn --batch-mode clean verify' }
            post { always { junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml' } }
        }
        stage('SonarQube') {
            steps {
                withSonarQubeEnv('sonarqube') {
                    sh 'mvn --batch-mode sonar:sonar'
                }
            }
        }
        stage('Quality gate') {
            steps { timeout(time: 10, unit: 'MINUTES') { waitForQualityGate abortPipeline: true } }
        }
        stage('Container validation') {
            when { expression { sh(script: 'command -v docker >/dev/null 2>&1', returnStatus: true) == 0 } }
            steps { sh 'docker compose config --quiet' }
        }
        stage('Build containers') {
            when {
                allOf {
                    branch 'main'
                    expression { sh(script: 'command -v docker >/dev/null 2>&1', returnStatus: true) == 0 }
                }
            }
            steps { sh 'docker compose build' }
        }
    }
}
