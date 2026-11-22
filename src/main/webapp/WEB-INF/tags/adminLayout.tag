<%@ tag language="java" pageEncoding="UTF-8" body-content="scriptless" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="p" uri="https://prestige.local/functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ attribute name="title" required="false" %>
<c:set var="active" value="rounded px-4 py-3 text-sm whitespace-nowrap bg-gradient-to-b from-champagne to-bronze font-semibold text-void"/>
<c:set var="idle" value="rounded px-4 py-3 text-sm whitespace-nowrap text-muted hover:bg-carbon"/>
<!DOCTYPE html>
<html lang="id" class="dark">
<head>
    <%@ include file="/WEB-INF/jspf/head.jspf" %>
    <title>${empty title ? 'SPM Command' : p:h(title)}</title>
</head>
<body class="min-h-screen lg:flex">
    <aside class="border-b border-gold/15 bg-panel p-5 lg:min-h-screen lg:w-64 lg:border-b-0 lg:border-r">
        <div class="font-display text-xl font-extrabold uppercase leading-tight">SPM Command
            <span class="caps block text-[10px] text-gold">Ops &amp; Telemetry</span>
        </div>
        <nav class="mt-8 flex gap-2 overflow-x-auto lg:flex-col">
            <a href="${base}/admin/dashboard" class="${fn:startsWith(currentPath, '/admin/dashboard') ? active : idle}">Operations Hub</a>
            <a href="${base}/admin/cars" class="${fn:startsWith(currentPath, '/admin/cars') ? active : idle}">Fleet Inventory</a>
            <a href="${base}/admin/bookings" class="${fn:startsWith(currentPath, '/admin/bookings') ? active : idle}">VIP Manifest</a>
        </nav>
        <div class="mt-8 space-y-3">
            <a href="${base}/" class="caps block text-muted hover:text-gold">&larr; Return to public atelier</a>
            <form method="post" action="${base}/logout">
                <input type="hidden" name="_csrf" value="${csrf}">
                <button class="caps text-muted hover:text-gold">Logout</button>
            </form>
        </div>
    </aside>
    <main class="flex-1 p-5 lg:p-10">
        <t:flash/>
        <jsp:doBody/>
    </main>
</body>
</html>
