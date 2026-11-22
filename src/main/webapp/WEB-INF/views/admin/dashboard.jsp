<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="p" uri="https://prestige.local/functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:adminLayout title="Operations Hub | SPM Command">
<div class="caps text-gold">Dispatch &amp; Fleet Core</div>
<h1 class="mt-2 font-display text-4xl font-extrabold uppercase">Operations Hub</h1>

<div class="mt-8 grid gap-4 sm:grid-cols-2 xl:grid-cols-5">
    <div class="card p-5">
        <div class="caps text-muted">Total Fleet</div>
        <div class="mt-2 font-mono text-2xl font-bold text-gold">${totalCars}</div>
    </div>
    <div class="card p-5">
        <div class="caps text-muted">Available</div>
        <div class="mt-2 font-mono text-2xl font-bold text-gold">${availableCars}</div>
    </div>
    <div class="card p-5">
        <div class="caps text-muted">Customers</div>
        <div class="mt-2 font-mono text-2xl font-bold text-gold">${totalCustomers}</div>
    </div>
    <div class="card p-5">
        <div class="caps text-muted">Pending Bookings</div>
        <div class="mt-2 font-mono text-2xl font-bold text-gold">${pendingBookings}</div>
    </div>
    <div class="card p-5">
        <div class="caps text-muted">Revenue (IDR)</div>
        <div class="mt-2 font-mono text-2xl font-bold text-gold">${p:idr(revenue)}</div>
    </div>
</div>

<div class="card mt-10 overflow-x-auto">
    <div class="caps border-b border-gold/20 p-4 text-gold">Latest Bookings</div>
    <table class="w-full min-w-[600px] text-left font-mono text-sm">
        <tbody>
            <c:forEach var="booking" items="${latestBookings}">
                <tr class="border-b border-white/5 odd:bg-carbon/30">
                    <td class="p-4 font-body">${p:h(booking.userName)}</td>
                    <td class="p-4">${p:h(booking.carName)}</td>
                    <td class="p-4 text-gold">IDR ${p:idr(booking.totalPrice)}</td>
                    <td class="p-4 uppercase">${p:h(booking.status)}</td>
                </tr>
            </c:forEach>
            <c:if test="${empty latestBookings}">
                <tr><td class="p-6 text-muted">Belum ada booking.</td></tr>
            </c:if>
        </tbody>
    </table>
</div>
</t:adminLayout>
