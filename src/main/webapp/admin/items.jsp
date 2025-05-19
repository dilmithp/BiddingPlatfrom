<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Item Management - BidMaster Admin</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <!-- Custom CSS -->
    <style>
        body { background: #f6f7fa; }
        .sidebar { min-width: 220px; max-width: 220px; background: #212529; min-height: 100vh; }
        .sidebar .nav-link, .sidebar .sidebar-header { color: #fff; }
        .sidebar .nav-link.active { background: #343a40; }
        .sidebar .sidebar-header { font-size: 1.3rem; font-weight: bold; padding: 1.5rem 1rem; }
        .content { flex: 1; padding: 2rem; }
        .card { border-radius: 0.5rem; }
        .avatar { width: 40px; height: 40px; object-fit: cover; border-radius: 50%; }
        .table td, .table th { vertical-align: middle; }
    </style>
</head>
<body>
    <div class="d-flex">
        <!-- Sidebar -->
        <div class="sidebar d-flex flex-column">
            <div class="sidebar-header d-flex align-items-center">
                <img src="../assets/images/logo.png" alt="BidMaster Logo" width="40" class="me-2">
                BidMaster Admin
            </div>
            <ul class="nav flex-column mb-auto">
                <li class="nav-item">
                    <a class="nav-link" href="dashboard.jsp"><i class="fas fa-tachometer-alt me-2"></i>Dashboard</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="AdminUserServlet"><i class="fas fa-users me-2"></i>Users</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link active" href="AdminItemServlet"><i class="fas fa-gavel me-2"></i>Items</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="AdminCategoryServlet"><i class="fas fa-tags me-2"></i>Categories</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="AdminTransactionServlet"><i class="fas fa-money-bill-wave me-2"></i>Transactions</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="AdminReportServlet"><i class="fas fa-chart-bar me-2"></i>Reports</a>
                </li>
                <li class="nav-item mt-4">
                    <a class="nav-link" href="../LogoutServlet"><i class="fas fa-sign-out-alt me-2"></i>Logout</a>
                </li>
            </ul>
        </div>
        
        <!-- Main Content -->
        <div class="content w-100">
            <!-- Top Navbar -->
            <nav class="navbar navbar-expand navbar-light bg-white shadow-sm rounded mb-4">
                <div class="container-fluid">
                    <span class="navbar-brand fw-bold">Item Management</span>
                    <div class="d-flex align-items-center">
                        <img src="../assets/images/admin-avatar.png" alt="Admin" class="avatar me-2">
                        <span class="fw-bold">${sessionScope.username}</span>
                    </div>
                </div>
            </nav>
            
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
            
            <!-- Search and Filter -->
            <div class="card mb-4">
                <div class="card-body">
                    <div class="row g-3">
                        <div class="col-md-6">
                            <form action="AdminItemServlet" method="get" class="d-flex">
                                <input type="hidden" name="action" value="search">
                                <input type="text" name="searchTerm" class="form-control me-2" placeholder="Search items..." value="${searchTerm}">
                                <button type="submit" class="btn btn-primary">
                                    <i class="fas fa-search"></i>
                                </button>
                            </form>
                        </div>
                        <div class="col-md-3">
                            <select class="form-select" id="statusFilter">
                                <option value="">All Statuses</option>
                                <option value="pending">Pending</option>
                                <option value="active">Active</option>
                                <option value="completed">Completed</option>
                                <option value="cancelled">Cancelled</option>
                            </select>
                        </div>
                        <div class="col-md-3">
                            <select class="form-select" id="categoryFilter">
                                <option value="">All Categories</option>
                                <c:forEach var="category" items="${categories}">
                                    <option value="${category.categoryId}">${category.categoryName}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- Items Table -->
            <div class="card shadow-sm">
                <div class="card-body">
                    <div class="table-responsive">
                        <table class="table table-hover align-middle">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Item</th>
                                    <th>Seller</th>
                                    <th>Category</th>
                                    <th>Current Price</th>
                                    <th>Bids</th>
                                    <th>End Date</th>
                                    <th>Status</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="item" items="${itemList}">
                                    <tr>
                                        <td>${item.itemId}</td>
                                        <td>
                                            <div class="d-flex align-items-center">
                                                <img src="${empty item.imageUrl ? '../assets/images/no-image.png' : item.imageUrl}" 
                                                     alt="${item.title}" class="me-2" width="40" height="40" style="object-fit: cover;">
                                                <div class="text-truncate" style="max-width: 200px;">${item.title}</div>
                                            </div>
                                        </td>
                                        <td>${item.sellerUsername}</td>
                                        <td>${item.categoryName}</td>
                                        <td>$<fmt:formatNumber value="${item.currentPrice}" pattern="#,##0.00"/></td>
                                        <td>${item.bidCount}</td>
                                        <td><fmt:formatDate value="${item.endTime}" pattern="MMM dd, yyyy" /></td>
                                        <td>
                                            <span class="badge bg-${item.status == 'active' ? 'success' : item.status == 'completed' ? 'secondary' : item.status == 'pending' ? 'warning' : 'danger'}">
                                                ${item.status}
                                            </span>
                                        </td>
                                        <td>
                                            <div class="btn-group btn-group-sm">
                                                <a href="../ItemDetailsServlet?id=${item.itemId}" class="btn btn-outline-primary" title="View">
                                                    <i class="fas fa-eye"></i>
                                                </a>
                                                <button type="button" class="btn btn-outline-secondary" 
                                                        data-bs-toggle="modal" data-bs-target="#statusModal" 
                                                        data-item-id="${item.itemId}" 
                                                        data-item-title="${item.title}" 
                                                        data-item-status="${item.status}" 
                                                        title="Change Status">
                                                    <i class="fas fa-exchange-alt"></i>
                                                </button>
                                                <button type="button" class="btn btn-outline-danger" 
                                                        data-bs-toggle="modal" data-bs-target="#deleteModal" 
                                                        data-item-id="${item.itemId}" 
                                                        data-item-title="${item.title}" 
                                                        title="Delete">
                                                    <i class="fas fa-trash"></i>
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                                
                                <c:if test="${empty itemList}">
                                    <tr>
                                        <td colspan="9" class="text-center py-4">
                                            <i class="fas fa-box fa-3x text-muted mb-3"></i>
                                            <p class="mb-0">No items found</p>
                                        </td>
                                    </tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
            
            <!-- Pagination -->
            <c:if test="${not empty itemList && totalPages > 1}">
                <nav class="mt-4">
                    <ul class="pagination justify-content-center">
                        <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                            <a class="page-link" href="AdminItemServlet?page=${currentPage - 1}" tabindex="-1">Previous</a>
                        </li>
                        
                        <c:forEach begin="1" end="${totalPages}" var="i">
                            <li class="page-item ${currentPage == i ? 'active' : ''}">
                                <a class="page-link" href="AdminItemServlet?page=${i}">${i}</a>
                            </li>
                        </c:forEach>
                        
                        <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                            <a class="page-link" href="AdminItemServlet?page=${currentPage + 1}">Next</a>
                        </li>
                    </ul>
                </nav>
            </c:if>
            
            <!-- Footer -->
            <footer class="text-center text-muted small mt-5">
                &copy; <%= java.time.Year.now().getValue() %> BidMaster Admin. All rights reserved.
            </footer>
        </div>
    </div>
    
    <!-- Status Change Modal -->
    <div class="modal fade" id="statusModal" tabindex="-1" aria-labelledby="statusModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="statusModalLabel">Change Item Status</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <form action="AdminItemServlet" method="post">
                    <div class="modal-body">
                        <input type="hidden" name="action" value="updateStatus">
                        <input type="hidden" id="statusItemId" name="itemId">
                        
                        <p>You are changing the status for: <strong id="statusItemTitle"></strong></p>
                        
                        <div class="mb-3">
                            <label for="status" class="form-label">Status</label>
                            <select class="form-select" id="status" name="status" required>
                                <option value="pending">Pending</option>
                                <option value="active">Active</option>
                                <option value="completed">Completed</option>
                                <option value="cancelled">Cancelled</option>
                            </select>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-primary">Save Changes</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
    
    <!-- Delete Confirmation Modal -->
    <div class="modal fade" id="deleteModal" tabindex="-1" aria-labelledby="deleteModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="deleteModalLabel">Confirm Delete</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <p>Are you sure you want to delete the item: <strong id="deleteItemTitle"></strong>?</p>
                    <p class="text-danger">This action cannot be undone.</p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <form action="AdminItemServlet" method="post">
                        <input type="hidden" name="action" value="delete">
                        <input type="hidden" id="deleteItemId" name="itemId">
                        <button type="submit" class="btn btn-danger">Delete</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    
    <script>
        // Status modal setup
        const statusModal = document.getElementById('statusModal');
        if (statusModal) {
            statusModal.addEventListener('show.bs.modal', function (event) {
                const button = event.relatedTarget;
                const itemId = button.getAttribute('data-item-id');
                const itemTitle = button.getAttribute('data-item-title');
                const itemStatus = button.getAttribute('data-item-status');
                
                document.getElementById('statusItemId').value = itemId;
                document.getElementById('statusItemTitle').textContent = itemTitle;
                document.getElementById('status').value = itemStatus;
            });
        }
        
        // Delete modal setup
        const deleteModal = document.getElementById('deleteModal');
        if (deleteModal) {
            deleteModal.addEventListener('show.bs.modal', function (event) {
                const button = event.relatedTarget;
                const itemId = button.getAttribute('data-item-id');
                const itemTitle = button.getAttribute('data-item-title');
                
                document.getElementById('deleteItemId').value = itemId;
                document.getElementById('deleteItemTitle').textContent = itemTitle;
            });
        }
        
        // Status filter
        document.getElementById('statusFilter').addEventListener('change', function() {
            filterItems();
        });
        
        // Category filter
        document.getElementById('categoryFilter').addEventListener('change', function() {
            filterItems();
        });
        
        function filterItems() {
            const statusFilter = document.getElementById('statusFilter').value;
            const categoryFilter = document.getElementById('categoryFilter').value;
            
            let url = 'AdminItemServlet?';
            if (statusFilter) {
                url += 'status=' + statusFilter + '&';
            }
            if (categoryFilter) {
                url += 'categoryId=' + categoryFilter;
            }
            
            window.location.href = url;
        }
    </script>
</body>
</html>
