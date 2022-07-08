pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
      		sh "chmod +x gradlew"
    		sh "./gradlew clean build --no-daemon"
            }
        }
        stage('Test') {
            steps {
                sh './gradlew test'
            }
        }
    }
}