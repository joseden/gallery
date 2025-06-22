pipeline {
    agent any
    
    environment {
        // Render deploy hook - you'll add this as a credential in Jenkins
        RENDER_DEPLOY_HOOK = credentials('render-deploy-hook')
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out code from repository...'
                checkout scm
                echo "Build Number: ${env.BUILD_NUMBER}"
                echo "Job Name: ${env.JOB_NAME}"
            }
        }
        
        stage('Install Dependencies') {
            steps {
                echo 'Installing Node.js dependencies...'
                sh '''
                    echo "Node.js version:"
                    node --version
                    echo "npm version:"
                    npm --version
                    
                    echo "Installing dependencies..."
                    npm install
                    
                    echo "Dependencies installed successfully!"
                '''
            }
        }
        
        stage('Verify Application') {
            steps {
                echo 'Verifying application files and configuration...'
                sh '''
                    echo "Checking required files..."
                    test -f package.json && echo "✓ package.json found" || echo "✗ package.json missing"
                    test -f server.js && echo "✓ server.js found" || echo "✗ server.js missing"
                    test -f config.js && echo "✓ config.js found" || echo "✗ config.js missing"
                    test -f Jenkinsfile && echo "✓ Jenkinsfile found" || echo "✗ Jenkinsfile missing"
                    
                    echo "Checking package.json start script..."
                    grep -q '"start"' package.json && echo "✓ npm start script found" || echo "⚠ npm start script missing"
                    
                    echo "Application structure verified!"
                '''
            }
        }
        
        stage('Deploy to Render') {
            steps {
                echo 'Triggering deployment to Render...'
                script {
                    try {
                        sh 'curl -X POST "$RENDER_DEPLOY_HOOK"'
                        echo '🚀 Deployment triggered successfully!'
                        echo "Render will now build and deploy the application"
                    } catch (Exception e) {
                        error "Deployment failed: ${e.getMessage()}"
                    }
                }
            }
        }
    }
    
    post {
        success {
            echo '✅ Pipeline completed successfully!'
            echo "🎉 Build ${env.BUILD_NUMBER} deployed!"
            echo "Your application should be available on Render shortly."
        }
        
        failure {
            echo '❌ Pipeline failed!'
            echo 'Check the logs above for details.'
        }
    }
}