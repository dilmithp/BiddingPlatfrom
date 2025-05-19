<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>BidMaster - Online Bidding Store</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="assets/css/style.css">
</head>
<body>
    <!-- Include Header -->
    <jsp:include page="includes/header.jsp" />
    
    <!-- Hero Section -->
    <section class="hero-section">
        <div class="container">
            <div class="row align-items-center">
                <div class="col-md-6">
                    <h1>Discover, Bid, Win!</h1>
                    <p class="lead">Your premier online bidding platform for unique items and amazing deals.</p>
                    <div class="mt-4">
                        <a href="ItemListServlet?action=listActive" class="btn btn-primary btn-lg me-3">
                            <i class="fas fa-gavel me-2"></i>Browse Auctions
                        </a>
                        <a href="RegisterServlet" class="btn btn-outline-light btn-lg">
                            <i class="fas fa-user-plus me-2"></i>Join Now
                        </a>
                    </div>
                </div>
                <div class="col-md-6">
                    <img src="assets/images/hero-image.png" alt="BidMaster Hero" class="img-fluid hero-image">
                </div>
            </div>
        </div>
    </section>
    
    <!-- Featured Categories -->
    <section class="py-5 bg-light">
        <div class="container">
            <h2 class="text-center mb-4">Browse Categories</h2>
            <div class="row g-4">
                <div class="col-md-3">
                    <div class="card category-card">
                        <div class="card-body text-center">
                            <i class="fas fa-laptop fa-3x mb-3 text-primary"></i>
                            <h5 class="card-title">Electronics</h5>
                            <p class="card-text">Latest gadgets and tech</p>
                            <a href="ItemListServlet?action=listByCategory&categoryId=1" class="btn btn-sm btn-outline-primary">Browse</a>
                        </div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="card category-card">
                        <div class="card-body text-center">
                            <i class="fas fa-gem fa-3x mb-3 text-primary"></i>
                            <h5 class="card-title">Collectibles</h5>
                            <p class="card-text">Rare and unique items</p>
                            <a href="ItemListServlet?action=listByCategory&categoryId=2" class="btn btn-sm btn-outline-primary">Browse</a>
                        </div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="card category-card">
                        <div class="card-body text-center">
                            <i class="fas fa-tshirt fa-3x mb-3 text-primary"></i>
                            <h5 class="card-title">Fashion</h5>
                            <p class="card-text">Clothing and accessories</p>
                            <a href="ItemListServlet?action=listByCategory&categoryId=3" class="btn btn-sm btn-outline-primary">Browse</a>
                        </div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="card category-card">
                        <div class="card-body text-center">
                            <i class="fas fa-home fa-3x mb-3 text-primary"></i>
                            <h5 class="card-title">Home & Garden</h5>
                            <p class="card-text">Furniture and decor</p>
                            <a href="ItemListServlet?action=listByCategory&categoryId=4" class="btn btn-sm btn-outline-primary">Browse</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>
    
    <!-- Ending Soon Items -->
    <section class="py-5">
        <div class="container">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2>Ending Soon</h2>
                <a href="ItemListServlet?action=listEndingSoon" class="btn btn-sm btn-outline-primary">View All</a>
            </div>
            <div class="row g-4">
                <c:forEach var="item" items="${endingSoonItems}" varStatus="loop">
                    <c:if test="${loop.index < 4}">
                        <div class="col-md-3">
                            <div class="card h-100 item-card">
                                <img src="${empty item.imageUrl ? 'assets/images/no-image.png' : item.imageUrl}" 
                                     class="card-img-top" alt="${item.title}">
                                <div class="card-body">
                                    <h5 class="card-title">${item.title}</h5>
                                    <p class="card-text text-truncate">${item.description}</p>
                                    <div class="d-flex justify-content-between align-items-center">
                                        <span class="badge bg-primary">$${item.currentPrice}</span>
                                        <small class="text-danger">Ends soon</small>
                                    </div>
                                </div>
                                <div class="card-footer">
                                    <a href="ItemDetailsServlet?id=${item.itemId}" class="btn btn-sm btn-primary w-100">View Details</a>
                                </div>
                            </div>
                        </div>
                    </c:if>
                </c:forEach>
            </div>
        </div>
    </section>
    
    <!-- How It Works -->
    <section class="py-5 bg-light">
        <div class="container">
            <h2 class="text-center mb-5">How It Works</h2>
            <div class="row g-4">
                <div class="col-md-4">
                    <div class="card h-100 how-it-works-card">
                        <div class="card-body text-center">
                            <div class="step-number">1</div>
                            <i class="fas fa-search fa-3x mb-3 text-primary"></i>
                            <h5 class="card-title">Browse & Find</h5>
                            <p class="card-text">Explore our wide range of items up for auction and find something you love.</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card h-100 how-it-works-card">
                        <div class="card-body text-center">
                            <div class="step-number">2</div>
                            <i class="fas fa-gavel fa-3x mb-3 text-primary"></i>
                            <h5 class="card-title">Place Your Bid</h5>
                            <p class="card-text">Enter your maximum bid and compete with other bidders to win the item.</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card h-100 how-it-works-card">
                        <div class="card-body text-center">
                            <div class="step-number">3</div>
                            <i class="fas fa-trophy fa-3x mb-3 text-primary"></i>
                            <h5 class="card-title">Win & Receive</h5>
                            <p class="card-text">If you're the highest bidder when the auction ends, the item is yours!</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>
    
    <!-- Testimonials -->
    <section class="py-5">
        <div class="container">
            <h2 class="text-center mb-5">What Our Users Say</h2>
            <div class="row">
                <div class="col-md-4 mb-4">
                    <div class="card testimonial-card">
                        <div class="card-body">
                            <div class="d-flex align-items-center mb-3">
                                <img src="assets/images/user1.jpg" alt="User" class="testimonial-img">
                                <div>
                                    <h5 class="mb-0">John Doe</h5>
                                    <small class="text-muted">Frequent Bidder</small>
                                </div>
                            </div>
                            <p class="card-text">"I've found so many unique items on BidMaster that I couldn't find anywhere else. The bidding process is exciting and fair!"</p>
                            <div class="text-warning">
                                <i class="fas fa-star"></i>
                                <i class="fas fa-star"></i>
                                <i class="fas fa-star"></i>
                                <i class="fas fa-star"></i>
                                <i class="fas fa-star"></i>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-md-4 mb-4">
                    <div class="card testimonial-card">
                        <div class="card-body">
                            <div class="d-flex align-items-center mb-3">
                                <img src="assets/images/user2.jpg" alt="User" class="testimonial-img">
                                <div>
                                    <h5 class="mb-0">Jane Smith</h5>
                                    <small class="text-muted">Seller</small>
                                </div>
                            </div>
                            <p class="card-text">"As a seller, I've been able to reach a wide audience and get fair prices for my items. The platform is easy to use and reliable."</p>
                            <div class="text-warning">
                                <i class="fas fa-star"></i>
                                <i class="fas fa-star"></i>
                                <i class="fas fa-star"></i>
                                <i class="fas fa-star"></i>
                                <i class="fas fa-star-half-alt"></i>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-md-4 mb-4">
                    <div class="card testimonial-card">
                        <div class="card-body">
                            <div class="d-flex align-items-center mb-3">
                                <img src="assets/images/user3.jpg" alt="User" class="testimonial-img">
                                <div>
                                    <h5 class="mb-0">Mike Johnson</h5>
                                    <small class="text-muted">Collector</small>
                                </div>
                            </div>
                            <p class="card-text">"BidMaster has helped me find rare collectibles that I've been searching for years. The community is great and the process is transparent."</p>
                            <div class="text-warning">
                                <i class="fas fa-star"></i>
                                <i class="fas fa-star"></i>
                                <i class="fas fa-star"></i>
                                <i class="fas fa-star"></i>
                                <i class="fas fa-star"></i>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>
    
    <!-- Call to Action -->
    <section class="py-5 bg-primary text-white cta-section">
        <div class="container text-center">
            <h2 class="mb-4">Ready to Start Bidding?</h2>
            <p class="lead mb-4">Join thousands of users who buy and sell on BidMaster every day.</p>
            <a href="RegisterServlet" class="btn btn-lg btn-light">Sign Up Now</a>
        </div>
    </section>
    
    <!-- Include Footer -->
    <jsp:include page="includes/footer.jsp" />
    
    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
    <!-- Custom JS -->
    <script src="assets/js/script.js"></script>
</body>
</html>
