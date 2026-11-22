<%@ tag language="java" pageEncoding="UTF-8" body-content="scriptless" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="p" uri="https://prestige.local/functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ attribute name="title" required="false" %>
<!DOCTYPE html>
<html lang="id" class="dark">
<head>
    <%@ include file="/WEB-INF/jspf/head.jspf" %>
    <title>${empty title ? 'Surabaya Prestige Motors' : p:h(title)}</title>
</head>
<body class="min-h-screen flex flex-col">
    <header class="sticky top-0 z-40 border-b border-gold/15 bg-void/85 backdrop-blur-xl">
        <div class="mx-auto flex max-w-[1440px] items-center justify-between px-5 py-4 lg:px-12">
            <a href="${base}/" class="font-display text-lg font-extrabold uppercase leading-tight tracking-tight">
                Surabaya Prestige
                <span class="caps block text-[10px] text-gold">Motorsport &amp; Luxury</span>
            </a>
            <nav class="hidden items-center gap-8 font-mono text-xs uppercase tracking-widest md:flex">
                <a href="${base}/" class="hover:text-gold">Home</a>
                <a href="${base}/fleet" class="hover:text-gold">Fleet</a>
                <a href="${base}/testimonials" class="hover:text-gold">Testimonials</a>
                <c:if test="${not empty authUser}">
                    <a href="${base}/favorites" class="hover:text-gold">Favorites</a>
                    <a href="${base}/my-bookings" class="hover:text-gold">My Bookings</a>
                </c:if>
            </nav>
            <div class="flex items-center gap-3">
                <c:choose>
                    <c:when test="${not empty authUser}">
                        <c:if test="${authUser.admin}">
                            <a href="${base}/admin/dashboard" class="btn-dark !px-4 !py-2 text-xs">Ops Console</a>
                        </c:if>
                        <form method="post" action="${base}/logout">
                            <input type="hidden" name="_csrf" value="${csrf}">
                            <button class="btn-gold !px-4 !py-2 text-xs">Logout</button>
                        </form>
                    </c:when>
                    <c:otherwise>
                        <a href="${base}/login" class="btn-dark !px-4 !py-2 text-xs">Login</a>
                        <a href="${base}/register" class="btn-gold !px-4 !py-2 text-xs">Register</a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </header>

    <main class="flex-1">
        <jsp:doBody/>
    </main>

    <footer class="border-t border-gold/15 bg-panel">
        <div class="mx-auto grid max-w-[1440px] gap-8 px-5 py-12 lg:grid-cols-3 lg:px-12">
            <div>
                <div class="font-display text-lg font-extrabold uppercase">Surabaya Prestige</div>
                <p class="mt-3 text-sm text-muted">High-performance automotive hire and bespoke track curation across Surabaya and East Java.</p>
            </div>
            <div>
                <div class="caps text-gold">Headquarters</div>
                <p class="mt-3 text-sm text-muted">Jl. Mayjen Sungkono No. 188<br>Surabaya Barat, Jawa Timur 60225</p>
            </div>
            <div>
                <div class="caps text-gold">Operational Hours</div>
                <p class="mt-3 font-mono text-sm text-muted">Concierge Desk: 24/7/365</p>
            </div>
        </div>
        <div class="border-t border-white/5 py-4 text-center font-mono text-[11px] uppercase tracking-widest text-muted">
            &copy; ${p:year()} Surabaya Prestige Motors
        </div>
    </footer>
</body>
</html>
