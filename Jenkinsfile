pipeline {
    agent any

    stages {
        stage('Compile') {

            steps {
		echo 'Compile project'
    		sh "chmod +x gradlew"
    		sh "./gradlew clean build --no-daemon"
                gradlew('clean','classes')
            }
        }
        stage('Unit Tests') {
            steps {
                gradlew('test')
            }
            post {
                always {
                    junit '**/build/test-results/test/TEST-*.xml'
                }
            }
        }
    }
}

def gradlew(String... args) {
    sh "./gradlew ${args.join(' ')} -s"
}