<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register - BidMaster</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <!-- Custom CSS -->
    <style>
        body {
            background-color: #f8f9fa;
        }
        .register-container {
            max-width: 600px;
            margin: 50px auto;
        }
        .card {
            border-radius: 10px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
        }
        .card-header {
            background-color: #fff;
            border-bottom: none;
            text-align: center;
            padding-top: 30px;
            padding-bottom: 20px;
        }
        .logo {
            width: 80px;
            height: 80px;
            margin-bottom: 15px;
        }
        .btn-primary {
            background-color: #3a5a78;
            border-color: #3a5a78;
            padding: 10px 20px;
        }
        .btn-primary:hover {
            background-color: #2c4760;
            border-color: #2c4760;
        }
        .form-control:focus {
            border-color: #3a5a78;
            box-shadow: 0 0 0 0.25rem rgba(58, 90, 120, 0.25);
        }
        .card-footer {
            background-color: #fff;
            border-top: none;
            text-align: center;
        }
        .form-label {
            font-weight: 500;
        }
        .password-strength {
            height: 5px;
            border-radius: 5px;
            margin-top: 5px;
        }
    </style>
</head>
<body>
    <div class="container register-container">
        <div class="card">
            <div class="card-header">
                <img src="assets/images/logo.png" alt="BidMaster Logo" class="logo">
                <h3 class="mb-0">Create an Account</h3>
                <p class="text-muted">Join BidMaster to start bidding and selling</p>
            </div>
            <div class="card-body p-4">
                <!-- Alert Messages -->
                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        <i class="fas fa-exclamation-circle me-2"></i>${errorMessage}
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>
                
                <!-- Registration Form -->
                <form action="RegisterServlet" method="post" id="registrationForm">
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="username" class="form-label">Username</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="fas fa-user"></i></span>
                                <input type="text" class="form-control ${not empty usernameError ? 'is-invalid' : ''}" 
                                       id="username" name="username" value="${username}" 
                                       placeholder="Choose a username" required>
                                <c:if test="${not empty usernameError}">
                                    <div class="invalid-feedback">${usernameError}</div>
                                </c:if>
                            </div>
                            <small class="form-text text-muted">3-50 characters, letters, numbers and underscores only</small>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="email" class="form-label">Email</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="fas fa-envelope"></i></span>
                                <input type="email" class="form-control ${not empty emailError ? 'is-invalid' : ''}" 
                                       id="email" name="email" value="${email}" 
                                       placeholder="Enter your email" required>
                                <c:if test="${not empty emailError}">
                                    <div class="invalid-feedback">${emailError}</div>
                                </c:if>
                            </div>
                        </div>
                    </div>
                    
                    <div class="mb-3">
                        <label for="fullName" class="form-label">Full Name</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="fas fa-user-tag"></i></span>
                            <input type="text" class="form-control ${not empty fullNameError ? 'is-invalid' : ''}" 
                                   id="fullName" name="fullName" value="${fullName}" 
                                   placeholder="Enter your full name" required>
                            <c:if test="${not empty fullNameError}">
                                <div class="invalid-feedback">${fullNameError}</div>
                            </c:if>
                        </div>
                    </div>
                    
                    <div class="mb-3">
                        <label for="contactNo" class="form-label">Contact Number</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="fas fa-phone"></i></span>
                            <input type="tel" class="form-control ${not empty contactNoError ? 'is-invalid' : ''}" 
                                   id="contactNo" name="contactNo" value="${contactNo}" 
                                   placeholder="Enter your contact number">
                            <c:if test="${not empty contactNoError}">
                                <div class="invalid-feedback">${contactNoError}</div>
                            </c:if>
                        </div>
                    </div>
                    
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="password" class="form-label">Password</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="fas fa-lock"></i></span>
                                <input type="password" class="form-control ${not empty passwordError ? 'is-invalid' : ''}" 
                                       id="password" name="password" 
                                       placeholder="Create a password" required>
                                <c:if test="${not empty passwordError}">
                                    <div class="invalid-feedback">${passwordError}</div>
                                </c:if>
                            </div>
                            <div class="password-strength bg-secondary opacity-25 mt-2"></div>
                            <small class="form-text text-muted">At least 8 characters with letters, numbers & symbols</small>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="confirmPassword" class="form-label">Confirm Password</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="fas fa-lock"></i></span>
                                <input type="password" class="form-control ${not empty confirmPasswordError ? 'is-invalid' : ''}" 
                                       id="confirmPassword" name="confirmPassword" 
                                       placeholder="Confirm your password" required>
                                <c:if test="${not empty confirmPasswordError}">
                                    <div class="invalid-feedback">${confirmPasswordError}</div>
                                </c:if>
                            </div>
                        </div>
                    </div>
                    
                    <div class="mb-3">
                        <label for="role" class="form-label">I want to</label>
                        <select class="form-select" id="role" name="role">
                            <option value="user" selected>Bid on items (Buyer)</option>
                            <option value="seller">Sell items (Seller)</option>
                            <option value="both">Both buy and sell</option>
                        </select>
                    </div>
                    
                    <div class="mb-4 form-check">
                        <input type="checkbox" class="form-check-input" id="termsCheck" name="termsCheck" required>
                        <label class="form-check-label" for="termsCheck">
                            I agree to the <a href="#" class="text-decoration-none">Terms of Service</a> and <a href="#" class="text-decoration-none">Privacy Policy</a>
                        </label>
                    </div>
                    
                    <div class="d-grid gap-2">
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-user-plus me-2"></i>Create Account
                        </button>
                    </div>
                </form>
            </div>
            <div class="card-footer py-3">
                <p class="mb-0">Already have an account? <a href="login.jsp" class="text-decoration-none">Sign in</a></p>
                <p class="mt-2 mb-0"><a href="index.jsp" class="text-decoration-none"><i class="fas fa-home me-1"></i>Back to Home</a></p>
            </div>
        </div>
    </div>
    
    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    
    <!-- Password Strength Script -->
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const passwordInput = document.getElementById('password');
            const strengthIndicator = document.querySelector('.password-strength');
            
            passwordInput.addEventListener('input', function() {
                const password = this.value;
                let strength = 0;
                
                if (password.length >= 8) strength += 25;
                if (password.match(/[A-Z]/)) strength += 25;
                if (password.match(/[0-9]/)) strength += 25;
                if (password.match(/[^A-Za-z0-9]/)) strength += 25;
                
                strengthIndicator.style.width = strength + '%';
                
                if (strength <= 25) {
                    strengthIndicator.className = 'password-strength bg-danger';
                } else if (strength <= 50) {
                    strengthIndicator.className = 'password-strength bg-warning';
                } else if (strength <= 75) {
                    strengthIndicator.className = 'password-strength bg-info';
                } else {
                    strengthIndicator.className = 'password-strength bg-success';
                }
            });
            
            // Password confirmation validation
            const confirmPasswordInput = document.getElementById('confirmPassword');
            const form = document.getElementById('registrationForm');
            
            form.addEventListener('submit', function(event) {
                if (passwordInput.value !== confirmPasswordInput.value) {
                    confirmPasswordInput.setCustomValidity("Passwords don't match");
                    event.preventDefault();
                } else {
                    confirmPasswordInput.setCustomValidity('');
                }
            });
            
            confirmPasswordInput.addEventListener('input', function() {
                if (passwordInput.value === this.value) {
                    this.setCustomValidity('');
                } else {
                    this.setCustomValidity("Passwords don't match");
                }
            });
        });
    </script>
</body>
</html>
