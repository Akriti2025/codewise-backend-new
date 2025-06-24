    package com.codewise.codewise_backend.ai;

    // Data Transfer Object for AI response
    public class AIResponse {
        private String answer;

        public AIResponse(String answer) {
            this.answer = answer;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }
    }
    