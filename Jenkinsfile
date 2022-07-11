pipeline {
import groovy.sql.Sql
node{
    def conn = Sql.newInstance("jdbc:mysql://localhost:3306/cms", "root", "", "com.mysql.jdbc.Driver")
    def rows = conn.rows("select username from users LIMIT 10")
    assert rows.size() == 10
    println rows.join('\n')
}
    agent any
    stages {
        stage('Build') {
            steps {
      		sh "chmod +x gradlew"
    		 sh "./gradlew bootJar"
            }
        }
        stage('Test') {
            steps {
                sh './gradlew test'
            }
        }
    }
}