<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="p" uri="https://prestige.local/functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:layout title="Testimonials | Surabaya Prestige Motors">
<div class="mx-auto max-w-[1440px] px-5 py-12 lg:px-12">
    <div class="caps text-gold">Client Voices</div>
    <h1 class="mt-2 font-display text-5xl font-extrabold uppercase">Testimonials</h1>
    <div class="mt-6"><t:flash/></div>

    <c:choose>
        <c:when test="${not empty authUser}">
            <form method="post" action="${base}/testimonials" class="card mt-8 max-w-2xl space-y-4 p-6">
                <input type="hidden" name="_csrf" value="${csrf}">
                <div class="caps text-gold">Share Your Experience</div>
                <div>
                    <label class="label">Rating</label>
                    <select name="rating" class="field">
                        <c:forEach var="i" begin="1" end="5">
                            <c:set var="score" value="${6 - i}"/>
                            <option value="${score}">${score} / 5</option>
                        </c:forEach>
                    </select>
                </div>
                <div>
                    <label class="label">Comment</label>
                    <textarea name="comment" rows="3" maxlength="500" class="field" required>${p:h(old.comment)}</textarea>
                </div>
                <button class="btn-gold">Submit</button>
            </form>
        </c:when>
        <c:otherwise>
            <p class="mt-8 text-muted"><a href="${base}/login" class="text-gold">Login</a> untuk menulis testimoni.</p>
        </c:otherwise>
    </c:choose>

    <div class="mt-10 grid gap-6 md:grid-cols-3">
        <c:forEach var="testimonial" items="${testimonials.items}">
            <t:testimonialCard testimonial="${testimonial}"/>
        </c:forEach>
        <c:if test="${empty testimonials.items}">
            <p class="text-muted">Belum ada testimoni.</p>
        </c:if>
    </div>
    <div class="mt-8"><t:pagination pager="${testimonials}" path="/testimonials"/></div>
</div>
</t:layout>
