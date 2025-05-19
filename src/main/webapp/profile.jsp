<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Profile - BidMaster</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <!-- Custom CSS -->
    <style>
        .profile-header {
            background-color: #3a5a78;
            color: white;
            padding: 2rem 0;
            margin-bottom: 2rem;
        }
        .profile-img {
            width: 150px;
            height: 150px;
            object-fit: cover;
            border: 5px solid white;
            border-radius: 50%;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        }
        .nav-pills .nav-link.active {
            background-color: #3a5a78;
        }
        .nav-pills .nav-link {
            color: #3a5a78;
        }
        .stats-card {
            border-left: 4px solid #3a5a78;
        }
        .rating-stars {
            color: #ffc107;
            font-size: 1.2rem;
        }
        .preview-image {
            max-height: 150px;
            object-fit: contain;
        }
    </style>
</head>
<body>
    <!-- Include Header -->
    <jsp:include page="includes/header.jsp" />
    
    <!-- Profile Header -->
    <div class="profile-header mt-5">
        <div class="container">
            <div class="row align-items-center">
                <div class="col-md-3 text-center">
                    <c:choose>
                        <c:when test="${not empty user.profileImage}">
                            <img src="${user.profileImage}" alt="${user.username}" class="profile-img">
                        </c:when>
                        <c:otherwise>
                            <img src="assets/images/default-avatar.png" alt="${user.username}" class="profile-img">
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="col-md-9">
                    <h1>${user.fullName}</h1>
                    <p class="lead mb-0">@${user.username}</p>
                    <p class="text-light">
                        <i class="fas fa-envelope me-2"></i>${user.email}
                        <c:if test="${not empty user.contactNo}">
                            <span class="ms-3"><i class="fas fa-phone me-2"></i>${user.contactNo}</span>
                        </c:if>
                    </p>
                    <p>
                        <span class="badge bg-light text-dark me-2">
                            <i class="fas fa-user me-1"></i>${user.role}
                        </span>
                        <span class="badge bg-light text-dark">
                            <i class="fas fa-calendar me-1"></i>Member since 
                            <fmt:formatDate value="${user.registrationDate}" pattern="MMM yyyy" />
                        </span>
                    </p>
                </div>
            </div>
        </div>
    </div>
    
    <div class="container mb-5">
        <!-- Alert Messages -->
        <c:if test="${not empty successMessage}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="fas fa-check-circle me-2"></i>${successMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>
        
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="fas fa-exclamation-circle me-2"></i>${errorMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>
        
        <!-- Profile Navigation -->
        <ul class="nav nav-pills mb-4" id="profileTab" role="tablist">
            <li class="nav-item" role="presentation">
                <button class="nav-link active" id="profile-tab" data-bs-toggle="pill" data-bs-target="#profile" 
                        type="button" role="tab" aria-controls="profile" aria-selected="true">
                    <i class="fas fa-user me-2"></i>Profile
                </button>
            </li>
            <li class="nav-item" role="presentation">
                <button class="nav-link" id="bids-tab" data-bs-toggle="pill" data-bs-target="#bids" 
                        type="button" role="tab" aria-controls="bids" aria-selected="false">
                    <i class="fas fa-gavel me-2"></i>My Bids
                </button>
            </li>
            <li class="nav-item" role="presentation">
                <button class="nav-link" id="watchlist-tab" data-bs-toggle="pill" data-bs-target="#watchlist" 
                        type="button" role="tab" aria-controls="watchlist" aria-selected="false">
                    <i class="fas fa-heart me-2"></i>Watchlist
                </button>
            </li>
            <c:if test="${user.role == 'seller' || user.role == 'admin'}">
                <li class="nav-item" role="presentation">
                    <button class="nav-link" id="items-tab" data-bs-toggle="pill" data-bs-target="#items" 
                            type="button" role="tab" aria-controls="items" aria-selected="false">
                        <i class="fas fa-box me-2"></i>My Items
                    </button>
                </li>
            </c:if>
            <li class="nav-item" role="presentation">
                <button class="nav-link" id="transactions-tab" data-bs-toggle="pill" data-bs-target="#transactions" 
                        type="button" role="tab" aria-controls="transactions" aria-selected="false">
                    <i class="fas fa-exchange-alt me-2"></i>Transactions
                </button>
            </li>
            <li class="nav-item" role="presentation">
                <button class="nav-link" id="feedback-tab" data-bs-toggle="pill" data-bs-target="#feedback" 
                        type="button" role="tab" aria-controls="feedback" aria-selected="false">
                    <i class="fas fa-star me-2"></i>Feedback
                </button>
            </li>
        </ul>
        
        <!-- Tab Content -->
        <div class="tab-content" id="profileTabContent">
            <!-- Profile Tab -->
            <div class="tab-pane fade show active" id="profile" role="tabpanel" aria-labelledby="profile-tab">
                <div class="card shadow-sm">
                    <div class="card-header bg-white">
                        <h5 class="card-title mb-0">Edit Profile</h5>
                    </div>
                    <div class="card-body">
                        <form action="ProfileServlet" method="post" enctype="multipart/form-data">
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label for="username" class="form-label">Username</label>
                                    <input type="text" class="form-control" id="username" value="${user.username}" readonly>
                                    <small class="text-muted">Username cannot be changed</small>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label for="email" class="form-label">Email Address</label>
                                    <input type="email" class="form-control ${not empty emailError ? 'is-invalid' : ''}" 
                                           id="email" name="email" value="${user.email}" required>
                                    <c:if test="${not empty emailError}">
                                        <div class="invalid-feedback">${emailError}</div>
                                    </c:if>
                                </div>
                            </div>
                            
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label for="fullName" class="form-label">Full Name</label>
                                    <input type="text" class="form-control ${not empty fullNameError ? 'is-invalid' : ''}" 
                                           id="fullName" name="fullName" value="${user.fullName}" required>
                                    <c:if test="${not empty fullNameError}">
                                        <div class="invalid-feedback">${fullNameError}</div>
                                    </c:if>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label for="contactNo" class="form-label">Contact Number</label>
                                    <input type="tel" class="form-control ${not empty contactError ? 'is-invalid' : ''}" 
                                           id="contactNo" name="contactNo" value="${user.contactNo}">
                                    <c:if test="${not empty contactError}">
                                        <div class="invalid-feedback">${contactError}</div>
                                    </c:if>
                                </div>
                            </div>
                            
                            <div class="mb-3">
                                <label for="profileImage" class="form-label">Profile Picture</label>
                                <input type="file" class="form-control ${not empty imageError ? 'is-invalid' : ''}" 
                                       id="profileImage" name="profileImage" accept="image/*">
                                <c:if test="${not empty imageError}">
                                    <div class="invalid-feedback">${imageError}</div>
                                </c:if>
                                <small class="text-muted">Max file size: 5MB. Supported formats: JPG, JPEG, PNG, GIF</small>
                                
                                <c:if test="${not empty user.profileImage}">
                                    <div class="mt-2">
                                        <p>Current Profile Picture:</p>
                                        <img src="${user.profileImage}" alt="Current Profile" class="preview-image border rounded">
                                    </div>
                                </c:if>
                            </div>
                            
                            <hr class="my-4">
                            
                            <h5>Change Password</h5>
                            <div class="row">
                                <div class="col-md-4 mb-3">
                                    <label for="currentPassword" class="form-label">Current Password</label>
                                    <input type="password" class="form-control" id="currentPassword" name="currentPassword">
                                </div>
                                <div class="col-md-4 mb-3">
                                    <label for="newPassword" class="form-label">New Password</label>
                                    <input type="password" class="form-control" id="newPassword" name="newPassword">
                                </div>
                                <div class="col-md-4 mb-3">
                                    <label for="confirmPassword" class="form-label">Confirm New Password</label>
                                    <input type="password" class="form-control" id="confirmPassword" name="confirmPassword">
                                </div>
                            </div>
                            <c:if test="${not empty passwordError}">
                                <div class="alert alert-danger">${passwordError}</div>
                            </c:if>
                            <small class="text-muted">Leave password fields empty if you don't want to change it</small>
                            
                            <div class="mt-4 text-end">
                                <button type="submit" class="btn btn-primary">
                                    <i class="fas fa-save me-2"></i>Save Changes
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
            
            <!-- My Bids Tab -->
            <div class="tab-pane fade" id="bids" role="tabpanel" aria-labelledby="bids-tab">
                <div class="card shadow-sm">
                    <div class="card-header bg-white">
                        <h5 class="card-title mb-0">My Bids</h5>
                    </div>
                    <div class="card-body">
                        <c:choose>
                            <c:when test="${not empty userBids}">
                                <div class="table-responsive">
                                    <table class="table table-hover">
                                        <thead>
                                            <tr>
                                                <th>Item</th>
                                                <th>Bid Amount</th>
                                                <th>Bid Time</th>
                                                <th>Status</th>
                                                <th>Actions</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="bid" items="${userBids}">
                                                <tr>
                                                    <td>
                                                        <a href="ItemDetailsServlet?id=${bid.itemId}" class="text-decoration-none">
                                                            ${bid.itemTitle}
                                                        </a>
                                                    </td>
                                                    <td>$<fmt:formatNumber value="${bid.bidAmount}" pattern="#,##0.00"/></td>
                                                    <td><fmt:formatDate value="${bid.bidTime}" pattern="MMM dd, yyyy HH:mm:ss" /></td>
                                                    <td>
                                                        <span class="badge bg-${bid.status == 'winning' ? 'success' : bid.status == 'outbid' ? 'warning' : 'primary'}">
                                                            ${bid.status}
                                                        </span>
                                                    </td>
                                                    <td>
                                                        <a href="ItemDetailsServlet?id=${bid.itemId}" class="btn btn-sm btn-outline-primary">
                                                            <i class="fas fa-eye"></i>
                                                        </a>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="text-center py-5">
                                    <i class="fas fa-gavel fa-4x text-muted mb-3"></i>
                                    <h5>You haven't placed any bids yet</h5>
                                    <p class="text-muted">Start bidding on items you're interested in!</p>
                                    <a href="ItemListServlet?action=listActive" class="btn btn-primary mt-3">
                                        <i class="fas fa-search me-2"></i>Browse Active Auctions
                                    </a>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
            
            <!-- Watchlist Tab -->
            <div class="tab-pane fade" id="watchlist" role="tabpanel" aria-labelledby="watchlist-tab">
                <div class="card shadow-sm">
                    <div class="card-header bg-white">
                        <h5 class="card-title mb-0">My Watchlist</h5>
                    </div>
                    <div class="card-body">
                        <c:choose>
                            <c:when test="${not empty watchlist}">
                                <div class="row g-4">
                                    <c:forEach var="watchlistItem" items="${watchlist}">
                                        <div class="col-md-6 col-lg-4">
                                            <div class="card h-100">
                                                <div class="position-relative">
                                                    <img src="${empty watchlistItem.itemImageUrl ? 'assets/images/no-image.png' : watchlistItem.itemImageUrl}" 
                                                         class="card-img-top" alt="${watchlistItem.itemTitle}" style="height: 180px; object-fit: cover;">
                                                    <form action="RemoveFromWatchlistServlet" method="post" class="position-absolute top-0 end-0 m-2">
                                                        <input type="hidden" name="itemId" value="${watchlistItem.itemId}">
                                                        <input type="hidden" name="fromWatchlist" value="true">
                                                        <button type="submit" class="btn btn-sm btn-danger" title="Remove from watchlist">
                                                            <i class="fas fa-times"></i>
                                                        </button>
                                                    </form>
                                                </div>
                                                <div class="card-body">
                                                    <h5 class="card-title text-truncate">${watchlistItem.itemTitle}</h5>
                                                    <div class="d-flex justify-content-between align-items-center mb-2">
                                                        <span class="fw-bold text-primary">$<fmt:formatNumber value="${watchlistItem.currentPrice}" pattern="#,##0.00"/></span>
                                                        <small class="text-muted countdown" data-end="${watchlistItem.endTime}"></small>
                                                    </div>
                                                </div>
                                                <div class="card-footer bg-white">
                                                    <a href="ItemDetailsServlet?id=${watchlistItem.itemId}" class="btn btn-sm btn-primary w-100">
                                                        <i class="fas fa-eye me-2"></i>View Details
                                                    </a>
                                                </div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="text-center py-5">
                                    <i class="fas fa-heart fa-4x text-muted mb-3"></i>
                                    <h5>Your watchlist is empty</h5>
                                    <p class="text-muted">Save items you're interested in to your watchlist!</p>
                                    <a href="ItemListServlet?action=listActive" class="btn btn-primary mt-3">
                                        <i class="fas fa-search me-2"></i>Browse Active Auctions
                                    </a>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
            
            <!-- My Items Tab (for sellers) -->
            <c:if test="${user.role == 'seller' || user.role == 'admin'}">
                <div class="tab-pane fade" id="items" role="tabpanel" aria-labelledby="items-tab">
                    <div class="card shadow-sm">
                        <div class="card-header bg-white d-flex justify-content-between align-items-center">
                            <h5 class="card-title mb-0">My Items</h5>
                            <a href="CreateItemServlet" class="btn btn-primary btn-sm">
                                <i class="fas fa-plus me-2"></i>Create New Auction
                            </a>
                        </div>
                        <div class="card-body">
                            <c:choose>
                                <c:when test="${not empty userItems}">
                                    <div class="table-responsive">
                                        <table class="table table-hover">
                                            <thead>
                                                <tr>
                                                    <th>Item</th>
                                                    <th>Current Price</th>
                                                    <th>Bids</th>
                                                    <th>End Time</th>
                                                    <th>Status</th>
                                                    <th>Actions</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach var="item" items="${userItems}">
                                                    <tr>
                                                        <td>
                                                            <div class="d-flex align-items-center">
                                                                <img src="${empty item.imageUrl ? 'assets/images/no-image.png' : item.imageUrl}" 
                                                                     alt="${item.title}" class="me-2" width="50" height="50" style="object-fit: cover;">
                                                                ${item.title}
                                                            </div>
                                                        </td>
                                                        <td>$<fmt:formatNumber value="${item.currentPrice}" pattern="#,##0.00"/></td>
                                                        <td>${item.bidCount}</td>
                                                        <td><fmt:formatDate value="${item.endTime}" pattern="MMM dd, yyyy HH:mm" /></td>
                                                        <td>
                                                            <span class="badge bg-${item.status == 'active' ? 'success' : item.status == 'completed' ? 'secondary' : 'primary'}">
                                                                ${item.status}
                                                            </span>
                                                        </td>
                                                        <td>
                                                            <div class="btn-group btn-group-sm">
                                                                <a href="ItemDetailsServlet?id=${item.itemId}" class="btn btn-outline-primary">
                                                                    <i class="fas fa-eye"></i>
                                                                </a>
                                                                <c:if test="${item.status == 'pending' || item.status == 'active'}">
                                                                    <a href="UpdateItemServlet?id=${item.itemId}" class="btn btn-outline-secondary">
                                                                        <i class="fas fa-edit"></i>
                                                                    </a>
                                                                </c:if>
                                                                <button type="button" class="btn btn-outline-danger" 
                                                                        onclick="confirmDeleteItem(${item.itemId}, '${item.title}')">
                                                                    <i class="fas fa-trash"></i>
                                                                </button>
                                                            </div>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="text-center py-5">
                                        <i class="fas fa-box fa-4x text-muted mb-3"></i>
                                        <h5>You haven't listed any items yet</h5>
                                        <p class="text-muted">Start selling by creating your first auction!</p>
                                        <a href="CreateItemServlet" class="btn btn-primary mt-3">
                                            <i class="fas fa-plus me-2"></i>Create New Auction
                                        </a>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </c:if>
            
            <!-- Transactions Tab -->
            <div class="tab-pane fade" id="transactions" role="tabpanel" aria-labelledby="transactions-tab">
                <div class="card shadow-sm">
                    <div class="card-header bg-white">
                        <h5 class="card-title mb-0">My Transactions</h5>
                    </div>
                    <div class="card-body">
                        <c:choose>
                            <c:when test="${not empty transactions}">
                                <ul class="nav nav-pills mb-3" id="transactionTabs" role="tablist">
                                    <li class="nav-item" role="presentation">
                                        <button class="nav-link active" id="purchases-tab" data-bs-toggle="pill" data-bs-target="#purchases" 
                                                type="button" role="tab" aria-controls="purchases" aria-selected="true">
                                            Purchases
                                        </button>
                                    </li>
                                    <c:if test="${user.role == 'seller' || user.role == 'admin'}">
                                        <li class="nav-item" role="presentation">
                                            <button class="nav-link" id="sales-tab" data-bs-toggle="pill" data-bs-target="#sales" 
                                                    type="button" role="tab" aria-controls="sales" aria-selected="false">
                                                Sales
                                            </button>
                                        </li>
                                    </c:if>
                                </ul>
                                
                                <div class="tab-content" id="transactionTabContent">
                                    <!-- Purchases Tab -->
                                    <div class="tab-pane fade show active" id="purchases" role="tabpanel" aria-labelledby="purchases-tab">
                                        <div class="table-responsive">
                                            <table class="table table-hover">
                                                <thead>
                                                    <tr>
                                                        <th>Item</th>
                                                        <th>Seller</th>
                                                        <th>Amount</th>
                                                        <th>Date</th>
                                                        <th>Status</th>
                                                        <th>Actions</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    <c:forEach var="transaction" items="${buyerTransactions}">
                                                        <tr>
                                                            <td>${transaction.itemTitle}</td>
                                                            <td>${transaction.sellerUsername}</td>
                                                            <td>$<fmt:formatNumber value="${transaction.amount}" pattern="#,##0.00"/></td>
                                                            <td><fmt:formatDate value="${transaction.transactionDate}" pattern="MMM dd, yyyy" /></td>
                                                            <td>
                                                                <span class="badge bg-${transaction.status == 'completed' ? 'success' : transaction.status == 'pending' ? 'warning' : 'danger'}">
                                                                    ${transaction.status}
                                                                </span>
                                                            </td>
                                                            <td>
                                                                <a href="ItemDetailsServlet?id=${transaction.itemId}" class="btn btn-sm btn-outline-primary">
                                                                    <i class="fas fa-eye"></i>
                                                                </a>
                                                                <c:if test="${transaction.status == 'completed' && !hasFeedback[transaction.transactionId]}">
                                                                    <button type="button" class="btn btn-sm btn-outline-warning" 
                                                                            onclick="openFeedbackModal(${transaction.transactionId}, ${transaction.sellerId}, '${transaction.sellerUsername}')">
                                                                        <i class="fas fa-star"></i>
                                                                    </button>
                                                                </c:if>
                                                            </td>
                                                        </tr>
                                                    </c:forEach>
                                                </tbody>
                                            </table>
                                        </div>
                                    </div>
                                    
                                    <!-- Sales Tab -->
                                    <c:if test="${user.role == 'seller' || user.role == 'admin'}">
                                        <div class="tab-pane fade" id="sales" role="tabpanel" aria-labelledby="sales-tab">
                                            <div class="table-responsive">
                                                <table class="table table-hover">
                                                    <thead>
                                                        <tr>
                                                            <th>Item</th>
                                                            <th>Buyer</th>
                                                            <th>Amount</th>
                                                            <th>Date</th>
                                                            <th>Status</th>
                                                            <th>Actions</th>
                                                        </tr>
                                                    </thead>
                                                    <tbody>
                                                        <c:forEach var="transaction" items="${sellerTransactions}">
                                                            <tr>
                                                                <td>${transaction.itemTitle}</td>
                                                                <td>${transaction.buyerUsername}</td>
                                                                <td>$<fmt:formatNumber value="${transaction.amount}" pattern="#,##0.00"/></td>
                                                                <td><fmt:formatDate value="${transaction.transactionDate}" pattern="MMM dd, yyyy" /></td>
                                                                <td>
                                                                    <span class="badge bg-${transaction.status == 'completed' ? 'success' : transaction.status == 'pending' ? 'warning' : 'danger'}">
                                                                        ${transaction.status}
                                                                    </span>
                                                                </td>
                                                                <td>
                                                                    <a href="ItemDetailsServlet?id=${transaction.itemId}" class="btn btn-sm btn-outline-primary">
                                                                        <i class="fas fa-eye"></i>
                                                                    </a>
                                                                </td>
                                                            </tr>
                                                        </c:forEach>
                                                    </tbody>
                                                </table>
                                            </div>
                                        </div>
                                    </c:if>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="text-center py-5">
                                    <i class="fas fa-exchange-alt fa-4x text-muted mb-3"></i>
                                    <h5>No transactions yet</h5>
                                    <p class="text-muted">Your transaction history will appear here</p>
                                    <a href="ItemListServlet?action=listActive" class="btn btn-primary mt-3">
                                        <i class="fas fa-search me-2"></i>Browse Active Auctions
                                    </a>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
            
            <!-- Feedback Tab -->
            <div class="tab-pane fade" id="feedback" role="tabpanel" aria-labelledby="feedback-tab">
                <div class="card shadow-sm">
                    <div class="card-header bg-white">
                        <h5 class="card-title mb-0">My Feedback</h5>
                    </div>
                    <div class="card-body">
                        <ul class="nav nav-pills mb-3" id="feedbackTabs" role="tablist">
                            <li class="nav-item" role="presentation">
                                <button class="nav-link active" id="received-tab" data-bs-toggle="pill" data-bs-target="#received" 
                                        type="button" role="tab" aria-controls="received" aria-selected="true">
                                    Feedback Received
                                </button>
                            </li>
                            <li class="nav-item" role="presentation">
                                <button class="nav-link" id="given-tab" data-bs-toggle="pill" data-bs-target="#given" 
                                        type="button" role="tab" aria-controls="given" aria-selected="false">
                                    Feedback Given
                                </button>
                            </li>
                        </ul>
                        
                        <div class="tab-content" id="feedbackTabContent">
                            <!-- Feedback Received Tab -->
                            <div class="tab-pane fade show active" id="received" role="tabpanel" aria-labelledby="received-tab">
                                <c:choose>
                                    <c:when test="${not empty feedbackReceived}">
                                        <div class="mb-4">
                                            <div class="d-flex align-items-center mb-2">
                                                <div class="rating-stars me-2">
                                                    <c:forEach begin="1" end="5" var="i">
                                                        <i class="fas fa-star${i <= averageRating ? '' : '-o'}"></i>
                                                    </c:forEach>
                                                </div>
                                                <h5 class="mb-0">${averageRating} out of 5</h5>
                                            </div>
                                            <p class="text-muted">Based on ${feedbackReceived.size()} reviews</p>
                                        </div>
                                        
                                        <div class="feedback-list">
                                            <c:forEach var="feedback" items="${feedbackReceived}">
                                                <div class="card mb-3">
                                                    <div class="card-body">
                                                        <div class="d-flex justify-content-between align-items-center mb-2">
                                                            <div class="d-flex align-items-center">
                                                                <img src="assets/images/default-avatar.png" alt="${feedback.fromUsername}" class="rounded-circle me-2" width="40">
                                                                <div>
                                                                    <h6 class="mb-0">${feedback.fromUsername}</h6>
                                                                    <small class="text-muted">
                                                                        <fmt:formatDate value="${feedback.feedbackDate}" pattern="MMM dd, yyyy" />
                                                                    </small>
                                                                </div>
                                                            </div>
                                                            <div class="rating-stars">
                                                                <c:forEach begin="1" end="5" var="i">
                                                                    <i class="fas fa-star${i <= feedback.rating ? '' : '-o'}"></i>
                                                                </c:forEach>
                                                            </div>
                                                        </div>
                                                        <p class="mb-1">${feedback.comment}</p>
                                                        <small class="text-muted">Item: ${feedback.itemTitle}</small>
                                                    </div>
                                                </div>
                                            </c:forEach>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="text-center py-5">
                                            <i class="fas fa-star fa-4x text-muted mb-3"></i>
                                            <h5>No feedback received yet</h5>
                                            <p class="text-muted">Feedback from others will appear here</p>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            
                            <!-- Feedback Given Tab -->
                            <div class="tab-pane fade" id="given" role="tabpanel" aria-labelledby="given-tab">
                                <c:choose>
                                    <c:when test="${not empty feedbackGiven}">
                                        <div class="feedback-list">
                                            <c:forEach var="feedback" items="${feedbackGiven}">
                                                <div class="card mb-3">
                                                    <div class="card-body">
                                                        <div class="d-flex justify-content-between align-items-center mb-2">
                                                            <div class="d-flex align-items-center">
                                                                <img src="assets/images/default-avatar.png" alt="${feedback.toUsername}" class="rounded-circle me-2" width="40">
                                                                <div>
                                                                    <h6 class="mb-0">To: ${feedback.toUsername}</h6>
                                                                    <small class="text-muted">
                                                                        <fmt:formatDate value="${feedback.feedbackDate}" pattern="MMM dd, yyyy" />
                                                                    </small>
                                                                </div>
                                                            </div>
                                                            <div class="rating-stars">
                                                                <c:forEach begin="1" end="5" var="i">
                                                                    <i class="fas fa-star${i <= feedback.rating ? '' : '-o'}"></i>
                                                                </c:forEach>
                                                            </div>
                                                        </div>
                                                        <p class="mb-1">${feedback.comment}</p>
                                                        <small class="text-muted">Item: ${feedback.itemTitle}</small>
                                                    </div>
                                                </div>
                                            </c:forEach>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="text-center py-5">
                                            <i class="fas fa-star fa-4x text-muted mb-3"></i>
                                            <h5>You haven't given any feedback yet</h5>
                                            <p class="text-muted">Feedback you give to others will appear here</p>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Delete Item Confirmation Modal -->
    <div class="modal fade" id="deleteItemModal" tabindex="-1" aria-labelledby="deleteItemModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-sm">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="deleteItemModalLabel">Confirm Delete</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <p>Are you sure you want to delete <span id="deleteItemName" class="fw-bold"></span>?</p>
                    <p class="text-danger small">This action cannot be undone.</p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <form action="DeleteItemServlet" method="post">
                        <input type="hidden" id="deleteItemId" name="itemId">
                        <button type="submit" class="btn btn-danger">Delete</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Feedback Modal -->
    <div class="modal fade" id="feedbackModal" tabindex="-1" aria-labelledby="feedbackModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="feedbackModalLabel">Leave Feedback</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <form action="FeedbackServlet" method="post" id="feedbackForm">
                        <input type="hidden" id="transactionId" name="transactionId">
                        <input type="hidden" id="toUserId" name="toUserId">
                        
                        <div class="mb-3">
                            <label class="form-label">Seller</label>
                            <input type="text" class="form-control" id="sellerUsername" readonly>
                        </div>
                        
                        <div class="mb-3">
                            <label class="form-label">Rating</label>
                            <div class="rating">
                                <div class="form-check form-check-inline">
                                    <input class="form-check-input" type="radio" name="rating" id="rating1" value="1" required>
                                    <label class="form-check-label" for="rating1">1</label>
                                </div>
                                <div class="form-check form-check-inline">
                                    <input class="form-check-input" type="radio" name="rating" id="rating2" value="2">
                                    <label class="form-check-label" for="rating2">2</label>
                                </div>
                                <div class="form-check form-check-inline">
                                    <input class="form-check-input" type="radio" name="rating" id="rating3" value="3">
                                    <label class="form-check-label" for="rating3">3</label>
                                </div>
                                <div class="form-check form-check-inline">
                                    <input class="form-check-input" type="radio" name="rating" id="rating4" value="4">
                                    <label class="form-check-label" for="rating4">4</label>
                                </div>
                                <div class="form-check form-check-inline">
                                    <input class="form-check-input" type="radio" name="rating" id="rating5" value="5" checked>
                                    <label class="form-check-label" for="rating5">5</label>
                                </div>
                            </div>
                        </div>
                        
                        <div class="mb-3">
                            <label for="comment" class="form-label">Comment</label>
                            <textarea class="form-control" id="comment" name="comment" rows="4" required></textarea>
                        </div>
                        
                        <div class="d-grid">
                            <button type="submit" class="btn btn-primary">Submit Feedback</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Include Footer -->
    <jsp:include page="includes/footer.jsp" />
    
    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    
    <script>
        // Profile image preview
        document.getElementById('profileImage')?.addEventListener('change', function(event) {
            const file = event.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = function(e) {
                    const previewContainer = document.createElement('div');
                    previewContainer.className = 'mt-2';
                    previewContainer.innerHTML = `
                        <p>New Profile Picture Preview:</p>
                        <img src="${e.target.result}" alt="Profile Preview" class="preview-image border rounded">
                    `;
                    
                    // Remove any existing preview
                    const existingPreview = document.querySelector('.preview-container');
                    if (existingPreview) {
                        existingPreview.remove();
                    }
                    
                    // Add new preview
                    previewContainer.classList.add('preview-container');
                    document.getElementById('profileImage').parentNode.appendChild(previewContainer);
                };
                reader.readAsDataURL(file);
            }
        });
        
        // Password confirmation validation
        document.getElementById('confirmPassword')?.addEventListener('input', function() {
            const newPassword = document.getElementById('newPassword').value;
            if (this.value !== newPassword) {
                this.setCustomValidity("Passwords don't match");
            } else {
                this.setCustomValidity('');
            }
        });
        
        // Delete item confirmation
        function confirmDeleteItem(itemId, itemTitle) {
            document.getElementById('deleteItemId').value = itemId;
            document.getElementById('deleteItemName').textContent = itemTitle;
            
            const deleteModal = new bootstrap.Modal(document.getElementById('deleteItemModal'));
            deleteModal.show();
        }
        
        // Open feedback modal
        function openFeedbackModal(transactionId, sellerId, sellerUsername) {
            document.getElementById('transactionId').value = transactionId;
            document.getElementById('toUserId').value = sellerId;
            document.getElementById('sellerUsername').value = sellerUsername;
            
            const feedbackModal = new bootstrap.Modal(document.getElementById('feedbackModal'));
            feedbackModal.show();
        }
        
        // Handle tab navigation from URL hash
        document.addEventListener('DOMContentLoaded', function() {
            const hash = window.location.hash;
            if (hash) {
                const tab = document.querySelector(`button[data-bs-target="${hash}"]`);
                if (tab) {
                    const bsTab = new bootstrap.Tab(tab);
                    bsTab.show();
                }
            }
        });
    </script>
</body>
</html>
