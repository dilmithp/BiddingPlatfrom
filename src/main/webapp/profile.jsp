<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${user.username} - Profile - BidMaster</title>
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
        .profile-avatar {
            width: 120px;
            height: 120px;
            border-radius: 50%;
            object-fit: cover;
            border: 4px solid white;
        }
        .nav-pills .nav-link.active {
            background-color: #3a5a78;
        }
        .nav-pills .nav-link {
            color: #3a5a78;
        }
        .badge-winning {
            background-color: #28a745;
        }
        .badge-outbid {
            background-color: #dc3545;
        }
        .badge-active {
            background-color: #17a2b8;
        }
        .action-btn {
            background-color: #3a5a78;
            border-color: #3a5a78;
        }
        .action-btn:hover {
            background-color: #2d4861;
            border-color: #2d4861;
        }
    </style>
</head>
<body>
    <!-- Include Header -->
    <jsp:include page="includes/header.jsp" />
    
    <!-- Profile Header -->
    <div class="profile-header">
        <div class="container">
            <div class="row align-items-center">
                <div class="col-md-2 text-center">
                    <img src="${empty user.profileImage ? 'assets/images/default-avatar.png' : user.profileImage}" 
                         alt="${user.username}" class="profile-avatar">
                </div>
                <div class="col-md-10">
                    <h1>@${user.username}</h1>
                    <p class="lead mb-0">${user.email}</p>
                    <p class="mb-0">${user.role} | Member since <fmt:formatDate value="${user.registrationDate}" pattern="MMM yyyy" /></p>
                </div>
            </div>
        </div>
    </div>
    
    <div class="container mb-5">
        <!-- Alert Messages -->
        <c:if test="${not empty sessionScope.successMessage}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="fas fa-check-circle me-2"></i>${sessionScope.successMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
            <% session.removeAttribute("successMessage"); %>
        </c:if>
        
        <c:if test="${not empty sessionScope.errorMessage}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="fas fa-exclamation-circle me-2"></i>${sessionScope.errorMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
            <% session.removeAttribute("errorMessage"); %>
        </c:if>
        
        <!-- Profile Tabs -->
        <ul class="nav nav-pills mb-4" id="profileTab" role="tablist">
            <li class="nav-item" role="presentation">
                <button class="nav-link active" id="bids-tab" data-bs-toggle="pill" data-bs-target="#bids" 
                        type="button" role="tab" aria-controls="bids" aria-selected="true">
                    <i class="fas fa-gavel me-2"></i>My Bids
                </button>
            </li>
            <li class="nav-item" role="presentation">
                <button class="nav-link" id="watchlist-tab" data-bs-toggle="pill" data-bs-target="#watchlist" 
                        type="button" role="tab" aria-controls="watchlist" aria-selected="false">
                    <i class="fas fa-heart me-2"></i>Watchlist
                </button>
            </li>
            <li class="nav-item" role="presentation">
                <button class="nav-link" id="listings-tab" data-bs-toggle="pill" data-bs-target="#listings" 
                        type="button" role="tab" aria-controls="listings" aria-selected="false">
                    <i class="fas fa-box me-2"></i>My Listings
                </button>
            </li>
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
            <!-- My Bids Tab -->
            <div class="tab-pane fade show active" id="bids" role="tabpanel" aria-labelledby="bids-tab">
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
                                                        <div class="d-flex align-items-center">
                                                            <img src="${empty bid.itemImageUrl ? 'assets/images/no-image.png' : bid.itemImageUrl}" 
                                                                 alt="${bid.itemTitle}" class="me-2" width="50" height="50" style="object-fit: cover;">
                                                            <div>
                                                                <div class="fw-bold">${bid.itemTitle}</div>
                                                                <small class="text-muted">Seller: ${bid.sellerUsername}</small>
                                                            </div>
                                                        </div>
                                                    </td>
                                                    <td>$<fmt:formatNumber value="${bid.bidAmount}" pattern="#,##0.00"/></td>
                                                    <td>
                                                        <fmt:formatDate value="${bid.bidTime}" pattern="MMM dd, yyyy HH:mm" />
                                                    </td>
                                                    <td>
                                                        <span class="badge ${bid.status == 'winning' ? 'badge-winning' : (bid.status == 'outbid' ? 'badge-outbid' : 'badge-active')}">
                                                            ${bid.status}
                                                        </span>
                                                    </td>
                                                    <td>
                                                        <div class="btn-group btn-group-sm">
                                                            <a href="ItemDetailsServlet?id=${bid.itemId}" class="btn action-btn text-white" title="View Item">
                                                                <i class="fas fa-eye"></i>
                                                            </a>
                                                            <c:if test="${bid.status != 'winning' && bid.itemStatus == 'active'}">
                                                                <button type="button" class="btn btn-warning" 
                                                                        onclick="showEditBidModal(${bid.bidId}, '${bid.itemTitle}', ${bid.bidAmount})" title="Edit Bid">
                                                                    <i class="fas fa-edit"></i>
                                                                </button>
                                                                <button type="button" class="btn btn-danger" 
                                                                        onclick="showDeleteBidModal(${bid.bidId}, '${bid.itemTitle}')" title="Delete Bid">
                                                                    <i class="fas fa-trash"></i>
                                                                </button>
                                                            </c:if>
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
                                    <i class="fas fa-gavel fa-4x text-muted mb-3"></i>
                                    <h5>You haven't placed any bids yet</h5>
                                    <p class="text-muted">Start bidding on items you're interested in!</p>
                                    <a href="BrowseItemsServlet" class="btn action-btn text-white mt-3">
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
                                <!-- Watchlist content -->
                            </c:when>
                            <c:otherwise>
                                <div class="text-center py-5">
                                    <i class="fas fa-heart fa-4x text-muted mb-3"></i>
                                    <h5>Your watchlist is empty</h5>
                                    <p class="text-muted">Save items you're interested in to your watchlist!</p>
                                    <a href="BrowseItemsServlet" class="btn action-btn text-white mt-3">
                                        <i class="fas fa-search me-2"></i>Browse Active Auctions
                                    </a>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
            
            <!-- My Listings Tab -->
            <div class="tab-pane fade" id="listings" role="tabpanel" aria-labelledby="listings-tab">
                <div class="card shadow-sm">
                    <div class="card-header bg-white">
                        <h5 class="card-title mb-0">My Listings</h5>
                    </div>
                    <div class="card-body">
                        <c:choose>
                            <c:when test="${not empty listings}">
                                <!-- Listings content -->
                            </c:when>
                            <c:otherwise>
                                <div class="text-center py-5">
                                    <i class="fas fa-box fa-4x text-muted mb-3"></i>
                                    <h5>You don't have any listings yet</h5>
                                    <p class="text-muted">Start selling by creating your first auction!</p>
                                    <a href="CreateItemServlet" class="btn action-btn text-white mt-3">
                                        <i class="fas fa-plus-circle me-2"></i>Create New Auction
                                    </a>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
            
            <!-- Transactions Tab -->
            <div class="tab-pane fade" id="transactions" role="tabpanel" aria-labelledby="transactions-tab">
                <div class="card shadow-sm">
                    <div class="card-header bg-white">
                        <h5 class="card-title mb-0">My Transactions</h5>
                    </div>
                    <div class="card-body">
                        <c:choose>
                            <c:when test="${not empty transactions}">
                                <!-- Transactions content -->
                            </c:when>
                            <c:otherwise>
                                <div class="text-center py-5">
                                    <i class="fas fa-exchange-alt fa-4x text-muted mb-3"></i>
                                    <h5>You don't have any transactions yet</h5>
                                    <p class="text-muted">Your transaction history will appear here</p>
                                    <a href="BrowseItemsServlet" class="btn action-btn text-white mt-3">
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
                <div class="row">
                    <!-- Feedback Received -->
                    <div class="col-md-6 mb-4">
                        <div class="card shadow-sm h-100">
                            <div class="card-header bg-white">
                                <h5 class="card-title mb-0">Feedback Received</h5>
                            </div>
                            <div class="card-body">
                                <div class="text-center mb-4">
                                    <div class="display-4 fw-bold text-primary">
                                        <fmt:formatNumber value="${userRating}" pattern="#.#" />
                                    </div>
                                    <div class="mb-2">
                                        <c:forEach begin="1" end="5" var="i">
                                            <i class="fas fa-star ${i <= userRating ? 'text-warning' : 'text-muted'}"></i>
                                        </c:forEach>
                                    </div>
                                    <div class="text-muted">Based on ${feedbackReceived.size()} reviews</div>
                                </div>
                                
                                <c:choose>
                                    <c:when test="${not empty feedbackReceived}">
                                        <c:forEach var="feedback" items="${feedbackReceived}">
                                            <div class="border-bottom pb-3 mb-3">
                                                <div class="d-flex justify-content-between mb-2">
                                                    <div>
                                                        <span class="fw-bold">${feedback.giverUsername}</span>
                                                        <span class="text-muted ms-2">
                                                            <fmt:formatDate value="${feedback.feedbackDate}" pattern="MMM dd, yyyy" />
                                                        </span>
                                                    </div>
                                                    <div>
                                                        <c:forEach begin="1" end="5" var="i">
                                                            <i class="fas fa-star ${i <= feedback.rating ? 'text-warning' : 'text-muted'}"></i>
                                                        </c:forEach>
                                                    </div>
                                                </div>
                                                <p class="mb-1">${feedback.comment}</p>
                                                <small class="text-muted">Item: ${feedback.itemTitle}</small>
                                            </div>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="text-center py-3">
                                            <p class="text-muted">Feedback from others will appear here</p>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Feedback Given -->
                    <div class="col-md-6 mb-4">
                        <div class="card shadow-sm h-100">
                            <div class="card-header bg-white">
                                <h5 class="card-title mb-0">Feedback Given</h5>
                            </div>
                            <div class="card-body">
                                <c:choose>
                                    <c:when test="${not empty feedbackGiven}">
                                        <c:forEach var="feedback" items="${feedbackGiven}">
                                            <div class="border-bottom pb-3 mb-3">
                                                <div class="d-flex justify-content-between mb-2">
                                                    <div>
                                                        <span class="fw-bold">To: ${feedback.receiverUsername}</span>
                                                        <span class="text-muted ms-2">
                                                            <fmt:formatDate value="${feedback.feedbackDate}" pattern="MMM dd, yyyy" />
                                                        </span>
                                                    </div>
                                                    <div>
                                                        <c:forEach begin="1" end="5" var="i">
                                                            <i class="fas fa-star ${i <= feedback.rating ? 'text-warning' : 'text-muted'}"></i>
                                                        </c:forEach>
                                                    </div>
                                                </div>
                                                <p class="mb-1">${feedback.comment}</p>
                                                <small class="text-muted">Item: ${feedback.itemTitle}</small>
                                            </div>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="text-center py-3">
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
    
    <!-- Edit Bid Modal -->
    <div class="modal fade" id="editBidModal" tabindex="-1" aria-labelledby="editBidModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="editBidModalLabel">Edit Bid</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <form action="UserBidsServlet" method="post">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" id="editBidId" name="bidId">
                    <div class="modal-body">
                        <div class="mb-3">
                            <h6 id="editItemTitle"></h6>
                        </div>
                        <div class="mb-3">
                            <label for="editBidAmount" class="form-label">New Bid Amount ($)</label>
                            <input type="number" class="form-control" id="editBidAmount" name="bidAmount" step="0.01" required>
                            <div class="form-text">Enter an amount higher than the current price</div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn action-btn text-white">Update Bid</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
    
    <!-- Delete Bid Modal -->
    <div class="modal fade" id="deleteBidModal" tabindex="-1" aria-labelledby="deleteBidModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="deleteBidModalLabel">Confirm Delete</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <p>Are you sure you want to delete your bid on <span id="deleteItemTitle" class="fw-bold"></span>?</p>
                    <p class="text-danger">This action cannot be undone.</p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <form action="UserBidsServlet" method="post">
                        <input type="hidden" name="action" value="delete">
                        <input type="hidden" id="deleteBidId" name="bidId">
                        <button type="submit" class="btn btn-danger">Delete</button>
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
        // Show edit bid modal
        function showEditBidModal(bidId, itemTitle, bidAmount) {
            document.getElementById('editBidId').value = bidId;
            document.getElementById('editItemTitle').textContent = itemTitle;
            document.getElementById('editBidAmount').value = bidAmount;
            document.getElementById('editBidAmount').min = bidAmount + 0.01;
            
            const editModal = new bootstrap.Modal(document.getElementById('editBidModal'));
            editModal.show();
        }
        
        // Show delete bid modal
        function showDeleteBidModal(bidId, itemTitle) {
            document.getElementById('deleteBidId').value = bidId;
            document.getElementById('deleteItemTitle').textContent = itemTitle;
            
            const deleteModal = new bootstrap.Modal(document.getElementById('deleteBidModal'));
            deleteModal.show();
        }
    </script>
</body>
</html>
