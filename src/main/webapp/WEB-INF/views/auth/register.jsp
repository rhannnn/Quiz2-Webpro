<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="p" uri="https://prestige.local/functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:layout title="Register | Surabaya Prestige Motors">
<div class="mx-auto max-w-md px-5 py-16">
    <div class="card p-8">
        <div class="caps text-gold">New Member</div>
        <h1 class="mt-2 font-display text-3xl font-extrabold uppercase">Register</h1>
        <div class="mt-6"><t:flash/></div>
        <form method="post" action="${base}/register" class="space-y-5">
            <input type="hidden" name="_csrf" value="${csrf}">
            <div>
                <label class="label">Nama Lengkap</label>
                <input type="text" name="name" value="${p:h(old.name)}" class="field" required>
            </div>
            <div>
                <label class="label">Email</label>
                <input type="email" name="email" value="${p:h(old.email)}" class="field" required>
            </div>
            <div>
                <label class="label">Password</label>
                <input type="password" name="password" class="field" required>
            </div>
            <div>
                <label class="label">Konfirmasi Password</label>
                <input type="password" name="password_confirmation" class="field" required>
            </div>
            <button class="btn-gold w-full">Create Account</button>
        </form>
        <p class="mt-6 text-sm text-muted">Sudah punya akun? <a href="${base}/login" class="text-gold">Login</a></p>
    </div>
</div>
</t:layout>
