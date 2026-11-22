<%@ tag language="java" pageEncoding="UTF-8" body-content="empty" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="pager" required="true" type="com.prestige.model.Page" %>
<%@ attribute name="path" required="true" %>
<%@ attribute name="query" required="false" %>
<c:set var="prefix" value="${base}${path}?${empty query ? '' : query}${empty query ? '' : '&'}page="/>
<c:if test="${pager.totalPages > 1}">
    <nav class="flex flex-wrap items-center gap-2 font-mono text-xs">
        <c:if test="${pager.page > 1}">
            <a href="${prefix}${pager.page - 1}" class="btn-dark !px-3 !py-1">&larr; Prev</a>
        </c:if>
        <c:forEach var="n" items="${pager.pages}">
            <a href="${prefix}${n}" class="${n == pager.page ? 'btn-gold' : 'btn-dark'} !px-3 !py-1">${n}</a>
        </c:forEach>
        <c:if test="${pager.page < pager.totalPages}">
            <a href="${prefix}${pager.page + 1}" class="btn-dark !px-3 !py-1">Next &rarr;</a>
        </c:if>
    </nav>
</c:if>
