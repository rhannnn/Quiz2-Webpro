<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="p" uri="https://prestige.local/functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="status" value="${pageContext.errorData.statusCode}"/>
<c:set var="detail" value="${requestScope['jakarta.servlet.error.message']}"/>
<t:layout title="Error | Surabaya Prestige Motors">
<div class="mx-auto max-w-2xl px-5 py-24 text-center">
    <div class="caps text-gold">Error</div>
    <h1 class="mt-2 font-display text-7xl font-extrabold uppercase">${status == 0 ? 500 : status}</h1>
    <p class="mt-6 text-lg text-muted">
        <c:choose>
            <c:when test="${status == 404}">Halaman yang Anda cari tidak ditemukan.</c:when>
            <c:when test="${status == 403}">${empty detail ? 'Akses ditolak.' : p:h(detail)}</c:when>
            <c:otherwise>Terjadi kesalahan pada server. Silakan coba lagi beberapa saat.</c:otherwise>
        </c:choose>
    </p>
    <a href="${base}/" class="btn-gold mt-8">Kembali ke Beranda</a>
</div>
</t:layout>
