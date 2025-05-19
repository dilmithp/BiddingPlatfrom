<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Admin Dashboard - BidMaster</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- Bootstrap 5 -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <style>
        body { background: #f6f7fa; }
        .sidebar { min-width: 220px; max-width: 220px; background: #212529; min-height: 100vh; }
        .sidebar .nav-link, .sidebar .sidebar-header { color: #fff; }
        .sidebar .nav-link.active { background: #343a40; }
        .sidebar .sidebar-header { font-size: 1.3rem; font-weight: bold; padding: 1.5rem 1rem; }
        .content { flex: 1; padding: 2rem; }
        .card { border-radius: 1rem; }
        .stats-card { min-height: 130px; }
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
                <a class="nav-link active" href="dashboard.jsp"><i class="fas fa-tachometer-alt me-2"></i>Dashboard</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="AdminUserServlet"><i class="fas fa-users me-2"></i>Users</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="AdminItemServlet"><i class="fas fa-gavel me-2"></i>Items</a>
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
                <span class="navbar-brand fw-bold">Dashboard</span>
                <div class="d-flex align-items-center">
                    <img src="../assets/images/admin-avatar.png" alt="Admin" class="avatar me-2">
                    <span class="fw-bold">${sessionScope.username}</span>
                </div>
            </div>
        </nav>
        <!-- Stats Cards -->
        <div class="row g-4 mb-4">
            <div class="col-md-3">
                <div class="card stats-card bg-primary text-white shadow-sm">
                    <div class="card-body d-flex align-items-center">
                        <div>
                            <div class="fs-4 mb-2"><i class="fas fa-users"></i></div>
                            <div class="fs-5 fw-bold">${stats.userCount}</div>
                            <div class="small">Total Users</div>
                        </div>
                        <div class="ms-auto">
                            <span class="badge bg-light text-primary">${stats.newUserCount} new</span>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card stats-card bg-success text-white shadow-sm">
                    <div class="card-body d-flex align-items-center">
                        <div>
                            <div class="fs-4 mb-2"><i class="fas fa-gavel"></i></div>
                            <div class="fs-5 fw-bold">${stats.activeItemCount}</div>
                            <div class="small">Active Auctions</div>
                        </div>
                        <div class="ms-auto">
                            <span class="badge bg-light text-success">${stats.newItemCount} new</span>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card stats-card bg-warning text-white shadow-sm">
                    <div class="card-body d-flex align-items-center">
                        <div>
                            <div class="fs-4 mb-2"><i class="fas fa-hand-paper"></i></div>
                            <div class="fs-5 fw-bold">${stats.bidCount}</div>
                            <div class="small">Total Bids</div>
                        </div>
                        <div class="ms-auto">
                            <span class="badge bg-light text-warning">${stats.newBidCount} today</span>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card stats-card bg-danger text-white shadow-sm">
                    <div class="card-body d-flex align-items-center">
                        <div>
                            <div class="fs-4 mb-2"><i class="fas fa-dollar-sign"></i></div>
                            <div class="fs-5 fw-bold">$${stats.revenue}</div>
                            <div class="small">Revenue</div>
                        </div>
                        <div class="ms-auto">
                            <span class="badge bg-light text-danger">$${stats.revenueIncrease} up</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!-- Recent Activity and Users -->
        <div class="row g-4 mb-4">
            <div class="col-lg-8">
                <div class="card shadow-sm h-100">
                    <div class="card-header bg-white d-flex justify-content-between align-items-center">
                        <h5 class="mb-0">Recent Activity</h5>
                    </div>
                    <div class="card-body">
                        <div class="table-responsive">
                            <table class="table table-hover align-middle">
                                <thead>
                                    <tr>
                                        <th>User</th>
                                        <th>Activity</th>
                                        <th>Item</th>
                                        <th>Time</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <!-- Example static data, replace with dynamic if available -->
                                    <tr>
                                        <td>john_doe</td>
                                        <td>Placed a bid</td>
                                        <td>Vintage Watch</td>
                                        <td>5 mins ago</td>
                                    </tr>
                                    <tr>
                                        <td>jane_smith</td>
                                        <td>Created an auction</td>
                                        <td>Antique Vase</td>
                                        <td>15 mins ago</td>
                                    </tr>
                                    <tr>
                                        <td>mike_johnson</td>
                                        <td>Won an auction</td>
                                        <td>Gaming Console</td>
                                        <td>1 hour ago</td>
                                    </tr>
                                    <tr>
                                        <td>sarah_williams</td>
                                        <td>Registered</td>
                                        <td>-</td>
                                        <td>2 hours ago</td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-lg-4">
                <div class="card shadow-sm h-100">
                    <div class="card-header bg-white d-flex justify-content-between align-items-center">
                        <h5 class="mb-0">New Users</h5>
                    </div>
                    <div class="card-body">
                        <ul class="list-group list-group-flush">
                            <c:forEach var="user" items="${recentUsers}">
                                <li class="list-group-item d-flex align-items-center">
                                    <img src="../assets/images/user-avatar.png" alt="User" class="avatar me-2">
                                    <div>
                                        <div class="fw-bold">${user.username}</div>
                                        <div class="small text-muted">${user.email}</div>
                                    </div>
                                    <span class="badge bg-primary ms-auto">${user.role}</span>
                                </li>
                            </c:forEach>
                            <c:if test="${empty recentUsers}">
                                <li class="list-group-item">No new users.</li>
                            </c:if>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
        <!-- Recent Items -->
        <div class="card shadow-sm mb-4">
            <div class="card-header bg-white d-flex justify-content-between align-items-center">
                <h5 class="mb-0">Recent Items</h5>
                <a href="AdminItemServlet" class="btn btn-sm btn-outline-primary">View All</a>
            </div>
            <div class="card-body">
                <div class="table-responsive">
                    <table class="table align-middle">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Item</th>
                                <th>Seller</th>
                                <th>Current Price</th>
                                <th>Bids</th>
                                <th>End Date</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="item" items="${recentItems}">
                                <tr>
                                    <td>${item.itemId}</td>
                                    <td>
                                        <img src="${empty item.imageUrl ? '../assets/images/no-image.png' : item.imageUrl}" alt="${item.title}" class="avatar me-2">
                                        ${item.title}
                                    </td>
                                    <td>${item.sellerUsername}</td>
                                    <td>$<fmt:formatNumber value="${item.currentPrice}" pattern="#,##0.00"/></td>
                                    <td>${item.bidCount}</td>
                                    <td><fmt:formatDate value="${item.endTime}" pattern="MMM dd, yyyy" /></td>
                                    <td>
                                        <span class="badge bg-${item.status == 'active' ? 'success' : item.status == 'completed' ? 'secondary' : 'primary'}">
                                            ${item.status}
                                        </span>
                                    </td>
                                    <td>
                                        <a href="../ItemDetailsServlet?id=${item.itemId}" class="btn btn-outline-primary btn-sm">
                                            <i class="fas fa-eye"></i>
                                        </a>
                                        <form action="AdminItemServlet" method="post" class="d-inline">
                                            <input type="hidden" name="action" value="delete">
                                            <input type="hidden" name="itemId" value="${item.itemId}">
                                            <button type="submit" class="btn btn-outline-danger btn-sm" onclick="return confirm('Delete this item?');">
                                                <i class="fas fa-trash"></i>
                                            </button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty recentItems}">
                                <tr><td colspan="8" class="text-center">No recent items.</td></tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
        <!-- Footer -->
        <footer class="text-center text-muted small mt-5">
            &copy; <%= java.time.Year.now().getValue() %> BidMaster Admin. All rights reserved.
        </footer>
    </div>
</div>
<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
