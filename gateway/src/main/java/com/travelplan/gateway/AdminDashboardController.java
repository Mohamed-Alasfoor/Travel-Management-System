package com.travelplan.gateway;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminDashboardController {
    @GetMapping(value = {"/", "/index.html"}, produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> dashboard() {
        String html = """
                <!doctype html>
                <html lang=\"en\">
                <head>
                  <meta charset=\"utf-8\">
                  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">
                  <title>Travel Plan Admin Dashboard</title>
                  <style>
                    :root { color-scheme: dark; --bg:#07111f; --panel:#10233f; --accent:#5eead4; --text:#f8fafc; --muted:#94a3b8; }
                    * { box-sizing: border-box; }
                    body { margin:0; font-family:Segoe UI, Arial, sans-serif; background:linear-gradient(135deg,var(--bg),#0f172a); color:var(--text); }
                    .shell { max-width:1200px; margin:0 auto; padding:24px; }
                    header { display:flex; justify-content:space-between; align-items:center; gap:16px; margin-bottom:24px; }
                    .card { min-width:0; background:rgba(16,35,63,0.95); border:1px solid rgba(148,163,184,0.2); border-radius:18px; padding:20px; box-shadow:0 15px 35px rgba(0,0,0,0.25); }
                    .grid { display:grid; gap:20px; grid-template-columns:1fr; }
                    form { display:grid; gap:10px; }
                    section form { grid-template-columns:repeat(auto-fit,minmax(190px,1fr)); align-items:center; padding:16px; margin:0 -4px 18px; border-radius:14px; background:rgba(7,17,31,0.36); }
                    section form button { min-height:42px; }
                    input, select, button { border-radius:10px; padding:10px 12px; border:1px solid rgba(148,163,184,0.25); font:inherit; }
                    button { cursor:pointer; background:var(--accent); color:#032b24; font-weight:700; }
                    button.secondary { background:transparent; color:var(--text); border-color:rgba(148,163,184,0.35); }
                    .pill { display:inline-block; padding:6px 10px; border-radius:999px; background:rgba(94,234,212,0.15); color:var(--accent); font-size:0.82rem; }
                    .table-wrap { width:100%; overflow-x:auto; border:1px solid rgba(148,163,184,0.16); border-radius:12px; }
                    table { width:100%; border-collapse:collapse; }
                    table.travels { min-width:1050px; }
                    table.users { min-width:760px; }
                    table.payment-methods { min-width:620px; }
                    th, td { padding:13px 14px; border-bottom:1px solid rgba(148,163,184,0.16); text-align:left; vertical-align:top; }
                    th { color:var(--accent); background:rgba(7,17,31,0.42); font-size:.78rem; letter-spacing:.04em; text-transform:uppercase; }
                    tbody tr:last-child td { border-bottom:0; }
                    tbody tr:hover { background:rgba(94,234,212,0.05); }
                    td:last-child { white-space:nowrap; }
                    .muted { color:var(--muted); }
                    .hidden { display:none; }
                    @media (max-width:800px) { .shell{padding:12px;} header{flex-direction:column; align-items:flex-start;} section form{grid-template-columns:1fr;} }
                  </style>
                </head>
                <body>
                  <div class=\"shell\">
                    <header class=\"card\">
                      <div>
                        <div class=\"pill\">Travel Plan Admin</div>
                        <h1 style=\"margin:8px 0 4px\">Operations Dashboard</h1>
                        <p class=\"muted\">Manage users, travel plans, and payment methods from one place.</p>
                      </div>
                      <form id=\"loginForm\" class=\"card\" style=\"padding:14px; min-width:280px;\">
                        <h3 style=\"margin:0 0 6px\">Admin Login</h3>
                        <input id=\"email\" type=\"email\" placeholder=\"admin@example.com\" required>
                        <input id=\"password\" type=\"password\" placeholder=\"Password\" required>
                        <button type=\"submit\">Sign in</button>
                      </form>
                    </header>

                    <div id=\"dashboard\" class=\"hidden\">
                      <div class=\"grid\">
                        <section class=\"card\">
                          <h2>Users</h2>
                          <form id=\"userForm\">
                            <input id=\"userEmail\" type=\"email\" placeholder=\"Email\" required>
                            <input id=\"userName\" placeholder=\"Display name\" required>
                            <input id=\"userPassword\" type=\"password\" placeholder=\"Password\" required>
                            <select id=\"userRole\">
                              <option value=\"ADMIN\">ADMIN</option>
                              <option value=\"USER\">USER</option>
                            </select>
                            <label><input id=\"userEnabled\" type=\"checkbox\" checked> Enabled</label>
                            <button type=\"submit\">Create user</button>
                          </form>
                          <div id=\"usersTable\"></div>
                        </section>

                        <section class=\"card\">
                          <h2>Travels</h2>
                          <form id=\"travelForm\">
                            <input id=\"travelDestination\" placeholder=\"Destination\" required>
                            <input id=\"travelDates\" placeholder=\"Dates\" required>
                            <input id=\"travelDuration\" type=\"number\" min=\"1\" placeholder=\"Duration (days)\" required>
                            <input id=\"travelActivities\" placeholder=\"Activities\" required>
                            <input id=\"travelAccommodation\" placeholder=\"Accommodation\" required>
                            <input id=\"travelTransportation\" placeholder=\"Transportation\" required>
                            <button type=\"submit\">Create travel</button>
                          </form>
                          <div id=\"travelsTable\"></div>
                        </section>

                        <section class=\"card\">
                          <h2>Payments</h2>
                          <form id=\"paymentForm\">
                            <input id=\"paymentName\" placeholder=\"Payment name\" required>
                            <select id=\"paymentProvider\" required>
                              <option value=\"STRIPE\">Stripe</option>
                              <option value=\"PAYPAL\">PayPal</option>
                            </select>
                            <label><input id=\"paymentEnabled\" type=\"checkbox\" checked> Enabled</label>
                            <button type=\"submit\">Create payment</button>
                          </form>
                          <div id=\"paymentsTable\"></div>
                        </section>
                      </div>
                    </div>
                  </div>
                  <script>
                    const loginForm = document.getElementById('loginForm');
                    const dashboard = document.getElementById('dashboard');
                    const userForm = document.getElementById('userForm');
                    const travelForm = document.getElementById('travelForm');
                    const paymentForm = document.getElementById('paymentForm');
                    let token = null;

                    async function request(path, options = {}) {
                      const headers = { ...(options.headers || {}) };
                      if (token) headers.Authorization = 'Bearer ' + token;
                      const response = await fetch(path, { ...options, headers });
                      const text = await response.text();
                      let body = null;
                      try { body = text ? JSON.parse(text) : null; } catch (e) { body = text; }
                      if (response.status === 401 && path !== '/api/auth/login') {
                        token = null;
                        dashboard.classList.add('hidden');
                        loginForm.classList.remove('hidden');
                        throw new Error('Your session expired. Please sign in again.');
                      }
                      if (!response.ok) throw new Error(body?.message || body || response.statusText);
                      return body;
                    }

                    loginForm.addEventListener('submit', async (event) => {
                      event.preventDefault();
                      try {
                        const response = await request('/api/auth/login', {
                          method: 'POST',
                          headers: { 'Content-Type': 'application/json' },
                          body: JSON.stringify({ email: document.getElementById('email').value, password: document.getElementById('password').value })
                        });
                        token = response.accessToken;
                        loginForm.classList.add('hidden');
                        dashboard.classList.remove('hidden');
                        await loadAll();
                      } catch (error) {
                        alert(error.message);
                      }
                    });

                    userForm.addEventListener('submit', async (event) => {
                      event.preventDefault();
                      try {
                        await request('/api/users', {
                          method: 'POST',
                          headers: { 'Content-Type': 'application/json' },
                          body: JSON.stringify({
                            email: document.getElementById('userEmail').value,
                            displayName: document.getElementById('userName').value,
                            password: document.getElementById('userPassword').value,
                            role: document.getElementById('userRole').value,
                            enabled: document.getElementById('userEnabled').checked
                          })
                        });
                        userForm.reset();
                        await loadUsers();
                      } catch (error) { alert(error.message); }
                    });

                    travelForm.addEventListener('submit', async (event) => {
                      event.preventDefault();
                      try {
                        await request('/api/travels', {
                          method: 'POST',
                          headers: { 'Content-Type': 'application/json' },
                          body: JSON.stringify({
                            destination: document.getElementById('travelDestination').value,
                            dates: document.getElementById('travelDates').value,
                            durationDays: Number(document.getElementById('travelDuration').value),
                            activities: document.getElementById('travelActivities').value,
                            accommodation: document.getElementById('travelAccommodation').value,
                            transportation: document.getElementById('travelTransportation').value
                          })
                        });
                        travelForm.reset();
                        await loadTravels();
                      } catch (error) { alert(error.message); }
                    });

                    paymentForm.addEventListener('submit', async (event) => {
                      event.preventDefault();
                      try {
                        await request('/api/payment-methods', {
                          method: 'POST',
                          headers: { 'Content-Type': 'application/json' },
                          body: JSON.stringify({
                            name: document.getElementById('paymentName').value,
                            provider: document.getElementById('paymentProvider').value,
                            enabled: document.getElementById('paymentEnabled').checked
                          })
                        });
                        paymentForm.reset();
                        await loadPayments();
                      } catch (error) { alert(error.message); }
                    });

                    async function loadAll() {
                      await Promise.all([loadUsers(), loadTravels(), loadPayments()]);
                    }

                    async function loadUsers() {
                      try {
                        const users = await request('/api/users');
                        document.getElementById('usersTable').innerHTML = renderTable(users, ['email', 'displayName', 'role', 'enabled'], 'users');
                      } catch (error) {
                        document.getElementById('usersTable').innerHTML = '<p class=\"muted\">'+error.message+'</p>';
                      }
                    }

                    async function loadTravels() {
                      try {
                        const travels = await request('/api/travels');
                        document.getElementById('travelsTable').innerHTML = renderTable(travels, ['destination', 'dates', 'durationDays', 'activities', 'accommodation', 'transportation'], 'travels');
                      } catch (error) {
                        document.getElementById('travelsTable').innerHTML = '<p class=\"muted\">'+error.message+'</p>';
                      }
                    }

                    async function loadPayments() {
                      try {
                        const payments = await request('/api/payment-methods');
                        document.getElementById('paymentsTable').innerHTML = renderTable(payments, ['name', 'provider', 'enabled'], 'payment-methods');
                      } catch (error) {
                        document.getElementById('paymentsTable').innerHTML = '<p class=\"muted\">'+error.message+'</p>';
                      }
                    }

                    function escapeHtml(value) {
                      return String(value ?? '-').replace(/[&<>\"']/g, character => ({'&':'&amp;','<':'&lt;','>':'&gt;','\"':'&quot;',"'":'&#039;'}[character]));
                    }

                    function renderTable(items, fields, resource) {
                      if (!items || !items.length) return '<p class=\"muted\">No records yet.</p>';
                      const labels = { displayName:'Name', durationDays:'Days', paymentMethod:'Payment method' };
                      const rows = items.map(item => '<tr>' + fields.map(field => '<td>' + escapeHtml(item[field] === true ? 'yes' : item[field] === false ? 'no' : item[field]) + '</td>').join('') + '<td><button class=\"secondary\" type=\"button\" data-action=\"edit\" data-resource=\"' + escapeHtml(resource) + '\" data-item=\"' + encodeURIComponent(JSON.stringify(item)) + '\">Edit</button> <button class=\"secondary\" type=\"button\" data-action=\"delete\" data-resource=\"' + escapeHtml(resource) + '\" data-id=\"' + escapeHtml(item.id) + '\">Delete</button></td></tr>').join('');
                      return '<div class=\"table-wrap\"><table class=\"' + escapeHtml(resource) + '\"><thead><tr>' + fields.map(field => '<th>' + escapeHtml(labels[field] || field.replace(/([A-Z])/g, ' $1')) + '</th>').join('') + '<th>Actions</th></tr></thead><tbody>' + rows + '</tbody></table></div>';
                    }

                    async function deleteItem(resource, id) {
                      if (!confirm('Delete this record? This cannot be undone.')) return;
                      try {
                        await request('/api/' + resource + '/' + id, { method: 'DELETE' });
                        await loadAll();
                      } catch (error) { alert(error.message); }
                    }

                    async function editItem(resource, item) {
                      item = JSON.parse(item);
                      let body;
                      if (resource === 'users') {
                        const displayName = prompt('Display name', item.displayName);
                        if (displayName === null) return;
                        body = { email:item.email, displayName, role:item.role, enabled:item.enabled };
                      } else if (resource === 'travels') {
                        const destination = prompt('Destination', item.destination);
                        if (destination === null) return;
                        body = { ...item, destination };
                      } else {
                        const name = prompt('Payment method name', item.name);
                        if (name === null) return;
                        body = { name, provider:item.provider, enabled:item.enabled };
                      }
                      try {
                        await request('/api/' + resource + '/' + item.id, { method:'PUT', headers:{'Content-Type':'application/json'}, body:JSON.stringify(body) });
                        await loadAll();
                      } catch (error) { alert(error.message); }
                    }

                    document.addEventListener('click', event => {
                      const button = event.target.closest('button[data-action]');
                      if (!button) return;
                      if (button.dataset.action === 'edit') {
                        editItem(button.dataset.resource, decodeURIComponent(button.dataset.item));
                      } else if (button.dataset.action === 'delete') {
                        deleteItem(button.dataset.resource, button.dataset.id);
                      }
                    });
                  </script>
                </body>
                </html>
                """;
        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(html);
    }
}
