<%@ tag language="java" pageEncoding="UTF-8" body-content="empty" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="p" uri="https://prestige.local/functions" %>
<c:if test="${not empty flashSuccess}">
    <div class="mb-6 rounded border border-gold/40 bg-gold/10 px-4 py-3 font-mono text-sm text-champagne">${p:h(flashSuccess)}</div>
</c:if>
<c:if test="${not empty flashErrors}">
    <div class="mb-6 rounded border border-red-400/40 bg-red-500/10 px-4 py-3 font-mono text-sm text-red-300">
        <c:forEach var="message" items="${flashErrors}">
            <div>${p:h(message)}</div>
        </c:forEach>
    </div>
</c:if>
