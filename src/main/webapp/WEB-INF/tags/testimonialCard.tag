<%@ tag language="java" pageEncoding="UTF-8" body-content="empty" %>
<%@ taglib prefix="p" uri="https://prestige.local/functions" %>
<%@ attribute name="testimonial" required="true" type="com.prestige.model.Testimonial" %>
<div class="card p-6">
    <div class="font-mono text-gold">${p:repeat('★', testimonial.rating)}<span class="text-white/20">${p:repeat('★', 5 - testimonial.rating)}</span></div>
    <p class="mt-4 italic text-muted">"${p:h(testimonial.comment)}"</p>
    <div class="caps mt-5 text-ink">${p:h(testimonial.userName)}</div>
</div>
