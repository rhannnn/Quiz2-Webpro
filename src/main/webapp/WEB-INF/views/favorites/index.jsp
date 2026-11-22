<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="p" uri="https://prestige.local/functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:layout title="Favorites | Surabaya Prestige Motors">
<div class="mx-auto max-w-[1440px] px-5 py-12 lg:px-12">
    <div class="caps text-gold">Personal Collection</div>
    <h1 class="mt-2 font-display text-5xl font-extrabold uppercase">My Favorites</h1>
    <div class="mt-6"><t:flash/></div>
    <div class="mt-8 grid gap-6 md:grid-cols-2 lg:grid-cols-3">
        <c:forEach var="car" items="${favoriteCars}">
            <t:carCard car="${car}"/>
        </c:forEach>
        <c:if test="${empty favoriteCars}">
            <p class="text-muted">Belum ada favorit. Tekan ikon hati pada kendaraan di <a href="${base}/fleet" class="text-gold">Fleet</a> untuk menyimpannya.</p>
        </c:if>
    </div>
</div>
</t:layout>
