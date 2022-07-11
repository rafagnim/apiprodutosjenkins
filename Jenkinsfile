pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
      		sh "chmod +x gradlew"
    		sh "./gradlew clean build"
    		sh "chmod 755 gradlew"
    		sh "./gradlew check"
            }
        }
        stage('Test') {
            steps {
                sh './gradlew test'
            }
        }
    }
}