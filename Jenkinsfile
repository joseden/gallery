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
                git branch: 'main', url: 'https://github.com/joseden/gallery.git'
                echo 'Repository cloned successfully'
            }
        }
    } 

    
}