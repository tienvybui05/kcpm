pipeline {
    agent any
    
    tools {
        nodejs 'NodeJS_20' [cite: 1]
    }
    
    environment {
        // --- 1. Cấu hình cho Backend (jira_automation.js) ---
        JIRA_API_TOKEN          = credentials('JIRA_API_TOKEN') [cite: 1]
        JIRA_DOMAIN             = 'caongoctanvo.atlassian.net' [cite: 1]
        JIRA_EMAIL              = 'caongoctanvo@gmail.com' [cite: 1, 2]
        JIRA_PROJECT_KEY        = 'TAP' [cite: 2]
        JIRA_SERVICE_ISSUE_TYPE = 'Epic' [cite: 2]
        JIRA_AUTOMATION_ISSUE_TYPE = 'Task' [cite: 2]
        JIRA_BUG_ISSUE_TYPE     = 'Subbug' [cite: 2]
        POSTMAN_COLLECTION_ID   = '55110231-c5612bea-0c70-4885-8563-a2a269fd1756' [cite: 2]
        API_BASE_URL            = 'http://gateway:8080/api' [cite: 2]

        // --- 2. Cấu hình cho Frontend (config.js / service.js) ---
        JIRA_BASE               = 'https://caongoctanvo.atlassian.net'
        EMAIL                   = 'caongoctanvo@gmail.com'
        TOKEN                   = credentials('JIRA_API_TOKEN')
        PROJECT_KEY             = 'TAP'
        JIRA_MODE               = 'jira' // Bật mode đẩy bug UI lên Jira
    }
    
    triggers {
        // Đúng 00:00 (Múi giờ UTC) chạy Full Regression Test
        cron('0 0 * * *') [cite: 3]
        // Quét code mới mỗi 2 tiếng
        pollSCM('H/2 * * * *') [cite: 3]
    }
    
    stages {
        stage('1. Kéo Code Từ Git') {
            steps {
                echo '📥 Đang kéo phiên bản code mới nhất từ kho lưu trữ...' [cite: 4]
                checkout scm [cite: 4]
            }
        }
        
        stage('2. Cài Đặt Môi Trường') {
            steps {
                echo '📦 Cài đặt thư viện Backend Test...' [cite: 5]
                dir('backend/test') {
                    sh 'npm install newman dotenv axios' [cite: 5]
                }
                
                echo '📦 Cài đặt thư viện Frontend UI Test...'
                dir('tool-test/codecept-jira-automation') {
                    sh 'npm install'
                    // Cài trình duyệt cho Playwright (cần thiết nếu agent Jenkins chạy trên Linux thuần)
                    sh 'npx playwright install chromium --with-deps'
                }
            }
        }
        
        stage('3. Chạy Automation Test & Đồng Bộ Jira') {
            steps {
                script {
                    // --- ĐỊNH NGHĨA HÀM CHẠY BACKEND CHO GỌN ---
                    def runBE = { service ->
                        dir('backend/test') {
                            if (service == "ALL") {
                                sh 'node jira_automation.js'
                            } else {
                                sh "node jira_automation.js \"${service}\""
                            }
                        }
                    }

                    // --- ĐỊNH NGHĨA HÀM CHẠY FRONTEND CHO GỌN ---
                    def runFE = { pattern ->
                        dir('tool-test/codecept-jira-automation') {
                            if (pattern == "ALL") {
                                sh 'node run.js --mode=jira'
                            } else {
                                sh "node run.js --mode=jira --file=${pattern} --workers=4"
                            }
                        }
                    }

                    // Kiểm tra xem Jenkins đang chạy vì đến giờ (00:00) hay vì có người Push Code 
                    def isTimer = currentBuild.getBuildCauses('hudson.triggers.TimerTrigger$TimerTriggerCause') [cite: 7]
                    
                    if (isTimer) {
                        echo '🕒 Chạy theo lịch 00:00 -> Đang test TOÀN BỘ hệ thống (BE + FE)...' [cite: 8]
                        runBE("ALL")
                        runFE("ALL")
                    } else {
                        // Đọc lời nhắn commit cuối cùng của Dev
                        def commitMsg = sh(script: 'git log -1 --pretty=%B', returnStdout: true).trim().toLowerCase() [cite: 11]
                        echo "💻 Lời nhắn commit: ${commitMsg}" [cite: 10, 11]
                        
                        // ================= MODULE STATION =================
                        if (commitMsg.contains('[be-station]')) {
                            echo '🚀 Bắt đầu test riêng lẻ Backend: station-service'
                            runBE("station-service")
                        } 
                        else if (commitMsg.contains('[fe-station]')) {
                            echo '🚀 Bắt đầu test riêng lẻ Frontend UI: station'
                            runFE("station/**/*_test.js")
                        }
                        else if (commitMsg.contains('[fix-station]')) {
                            echo '🚀 Bắt đầu test luồng FIX: Backend station-service + Frontend UI station'
                            runBE("station-service")
                            runFE("station/**/*_test.js")
                        }
                        
                        // ================= MODULE USERS / REGISTER =================
                        else if (commitMsg.contains('[be-users]')) {
                            echo '🚀 Bắt đầu test riêng lẻ Backend: users-service' [cite: 14]
                            runBE("users-service")
                        }
                        else if (commitMsg.contains('[fe-users]')) {
                            echo '🚀 Bắt đầu test riêng lẻ Frontend UI: users'
                            runFE("users/**/*_test.js")
                        }
                        else if (commitMsg.contains('[fix-users]')) {
                            echo '🚀 Bắt đầu test luồng FIX: Backend users-service + Frontend UI users'
                            runBE("users-service")
                            runFE("users/**/*_test.js")
                        }

                        // ================= MODULE VEHICLE =================
                        else if (commitMsg.contains('[be-vehicle]')) {
                            echo '🚀 Bắt đầu test riêng lẻ Backend: vehicle-service' [cite: 15]
                            runBE("vehicle-service")
                        }
                        else if (commitMsg.contains('[fe-vehicle]')) {
                            echo '🚀 Bắt đầu test riêng lẻ Frontend UI: vehicle'
                            runFE("vehicle/**/*_test.js")
                        }
                        else if (commitMsg.contains('[fix-vehicle]')) {
                            echo '🚀 Bắt đầu test luồng FIX: Backend vehicle-service + Frontend UI vehicle'
                            runBE("vehicle-service")
                            runFE("vehicle/**/*_test.js")
                        }

                        // ================= MODULE TRANSACTION =================
                        else if (commitMsg.contains('[be-transaction]')) {
                            echo '🚀 Bắt đầu test riêng lẻ Backend: transaction-service' [cite: 15]
                            runBE("transaction-service")
                        }
                        else if (commitMsg.contains('[fe-transaction]')) {
                            echo '🚀 Bắt đầu test riêng lẻ Frontend UI: transaction'
                            runFE("transaction/**/*_test.js")
                        }
                        else if (commitMsg.contains('[fix-transaction]')) {
                            echo '🚀 Bắt đầu test luồng FIX: Backend transaction-service + Frontend UI transaction'
                            runBE("transaction-service")
                            runFE("transaction/**/*_test.js")
                        }

                        // ================= MODULE BATTERY =================
                        else if (commitMsg.contains('[be-battery]')) {
                            echo '🚀 Bắt đầu test riêng lẻ Backend: battery-service' [cite: 15]
                            runBE("battery-service")
                        }
                        else if (commitMsg.contains('[fe-battery]')) {
                            echo '🚀 Bắt đầu test riêng lẻ Frontend UI: battery'
                            runFE("battery/**/*_test.js")
                        }
                        else if (commitMsg.contains('[fix-battery]')) {
                            echo '🚀 Bắt đầu test luồng FIX: Backend battery-service + Frontend UI battery'
                            runBE("battery-service")
                            runFE("battery/**/*_test.js")
                        }

                        // ================= MODULE FEEDBACK =================
                        else if (commitMsg.contains('[be-feedback]')) {
                            echo '🚀 Bắt đầu test riêng lẻ Backend: feedback-service' [cite: 15]
                            runBE("feedback-service")
                        }
                        else if (commitMsg.contains('[fe-feedback]')) {
                            echo '🚀 Bắt đầu test riêng lẻ Frontend UI: feedback'
                            runFE("feedback/**/*_test.js")
                        }
                        else if (commitMsg.contains('[fix-feedback]')) {
                            echo '🚀 Bắt đầu test luồng FIX: Backend feedback-service + Frontend UI feedback'
                            runBE("feedback-service")
                            runFE("feedback/**/*_test.js")
                        }

                        // ================= MODULE SUBSCRIPTION =================
                        else if (commitMsg.contains('[be-subscription]')) {
                            echo '🚀 Bắt đầu test riêng lẻ Backend: subscription-service' [cite: 15]
                            runBE("subscription-service")
                        }
                        else if (commitMsg.contains('[fe-subscription]')) {
                            echo '🚀 Bắt đầu test riêng lẻ Frontend UI: subscription'
                            runFE("subscription/**/*_test.js")
                        }
                        else if (commitMsg.contains('[fix-subscription]')) {
                            echo '🚀 Bắt đầu test luồng FIX: Backend subscription-service + Frontend UI subscription'
                            runBE("subscription-service")
                            runFE("subscription/**/*_test.js")
                        }

                        // ================= MẶC ĐỊNH (FULL) =================
                        else {
                            echo '⚠️ Không tìm thấy [tag] chỉ định trong commit -> Test TOÀN BỘ hệ thống (BE + FE) cho an toàn.' [cite: 20]
                            runBE("ALL")
                            runFE("ALL")
                        }
                    }
                }
            }
        }
    }
    
    post {
        always {
            echo '🏁 Tiến trình CI/CD hoàn tất, tự động dọn dẹp môi trường và thu thập hình ảnh lỗi (nếu có).' [cite: 26]
            // Lưu lại các ảnh chụp màn hình UI bị lỗi vào dashboard Jenkins để dễ xem
            archiveArtifacts artifacts: 'tool-test/codecept-jira-automation/output/*.png', allowEmptyArchive: true
        }
        success {
            echo '✅ Tuyệt vời! Tất cả các ca kiểm thử đều Pass. Không có Bug mới.' [cite: 23]
        }
        failure {
            echo '⚠️ Phát hiện lỗi (Bug). Hệ thống đã tự động đẩy/cập nhật thông tin lên thẻ Jira cho Dev.' [cite: 24, 25]
        }
    }
}