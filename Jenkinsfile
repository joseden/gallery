pipeline {
    agent any
    
    tools {
        nodejs 'Node18'
    }
    
    environment {
        RENDER_DEPLOY_HOOK = credentials('render-deploy-hook')
        SLACK_TOKEN = credentials('slack-bot-token')
        RENDER_APP_URL = 'https://gallery-fa8q.onrender.com'
    }
    
    stages {
        stage('Clone Repository') {
            steps {
                git branch: 'main', url:'https://github.com/joseden/gallery'
                    
                }
            }
        }
        
        stage('Install Dependencies') {
            steps {
                echo 'Installing Node.js dependencies...'
                sh 'npm install'
            }
        }
        
        stage('Run Tests') {
            steps {
                echo 'Running application tests...'
                sh 'npm test'
            }
            post {
                failure {
                    emailext (
                        subject: "Test Failed: ${env.JOB_NAME} - ${env.BUILD_NUMBER}",
                        body: "Tests failed in build ${env.BUILD_NUMBER}. Check console at ${env.BUILD_URL}",
                        to: "${env.CHANGE_AUTHOR_EMAIL}"
                    )
                }
            }
        }
        
}       