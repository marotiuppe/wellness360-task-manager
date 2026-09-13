/**
 * Wellness360 Task Management System — Single Page Application JS Client
 */

document.addEventListener('DOMContentLoaded', () => {
  // Global State
  const state = {
    token: localStorage.getItem('jwt_token') || null,
    username: localStorage.getItem('jwt_username') || null,
    tasks: [],
    activeFilter: 'all',
    searchQuery: '',
    deleteTaskId: null
  };

  // DOM Elements
  const DOM = {
    loginView: document.getElementById('login-view'),
    appView: document.getElementById('app-view'),
    tasksGrid: document.getElementById('tasks-grid'),
    statTotal: document.getElementById('stat-total'),
    statPending: document.getElementById('stat-pending'),
    statProgress: document.getElementById('stat-progress'),
    statCompleted: document.getElementById('stat-completed'),
    userInfo: document.getElementById('user-info'),
    userDisplayName: document.getElementById('user-display-name'),
    userDisplayRole: document.getElementById('user-display-role'),
    btnLogout: document.getElementById('btn-logout'),
    btnCreateTaskOpen: document.getElementById('btn-create-task-open'),
    searchInput: document.getElementById('search-input'),
    filterBtns: document.querySelectorAll('.tab-btn'),
    // Modals
    modalCreate: document.getElementById('modal-create'),
    modalEdit: document.getElementById('modal-edit'),
    modalDelete: document.getElementById('modal-delete'),
    // Forms
    formPageLogin: document.getElementById('form-login-page'),
    formCreate: document.getElementById('form-create-task'),
    formEdit: document.getElementById('form-edit-task'),
    btnConfirmDelete: document.getElementById('btn-confirm-delete'),
    toastContainer: document.getElementById('toast-container')
  };

  // Initialize UI & Auth State
  init();

  function init() {
    bindEvents();
    if (state.token && state.username) {
      showDashboardView();
      fetchTasks();
    } else {
      showLoginView();
    }
  }

  // Bind Event Listeners
  function bindEvents() {
    // Auth
    DOM.formPageLogin.addEventListener('submit', handleLogin);
    DOM.btnLogout.addEventListener('click', logout);

    // Embedded Modals
    const btnSwagger = document.getElementById('btn-swagger-open');
    const btnH2 = document.getElementById('btn-h2-open');
    const modalSwagger = document.getElementById('modal-swagger');
    const modalH2 = document.getElementById('modal-h2');

    if (btnSwagger && modalSwagger) {
      btnSwagger.addEventListener('click', () => openModal(modalSwagger));
    }
    if (btnH2 && modalH2) {
      btnH2.addEventListener('click', () => openModal(modalH2));
    }

    // Create Task
    DOM.btnCreateTaskOpen.addEventListener('click', () => openModal(DOM.modalCreate));
    DOM.formCreate.addEventListener('submit', handleCreateTask);

    // Edit Task
    DOM.formEdit.addEventListener('submit', handleEditTask);

    // Delete Task
    DOM.btnConfirmDelete.addEventListener('click', handleDeleteTask);

    // Close Modals
    document.querySelectorAll('[data-close]').forEach(btn => {
      btn.addEventListener('click', (e) => {
        const modalId = e.target.getAttribute('data-close');
        closeModal(document.getElementById(modalId));
      });
    });

    // Filter Tabs
    DOM.filterBtns.forEach(btn => {
      btn.addEventListener('click', () => {
        DOM.filterBtns.forEach(b => b.classList.remove('active'));
        btn.classList.add('active');
        state.activeFilter = btn.getAttribute('data-filter');
        renderTasks();
      });
    });

    // Search Input
    DOM.searchInput.addEventListener('input', (e) => {
      state.searchQuery = e.target.value.toLowerCase();
      renderTasks();
    });
  }

  // Show Login View Screen
  function showLoginView() {
    DOM.loginView.style.display = 'flex';
    DOM.appView.style.display = 'none';
    DOM.userInfo.style.display = 'none';
    DOM.btnLogout.style.display = 'none';
    DOM.btnCreateTaskOpen.style.display = 'none';
  }

  // Show Dashboard View Screen
  function showDashboardView() {
    DOM.loginView.style.display = 'none';
    DOM.appView.style.display = 'block';
    DOM.userInfo.style.display = 'flex';
    DOM.userDisplayName.textContent = state.username;
    DOM.userDisplayRole.textContent = (state.username === 'admin') ? 'ADMIN' : 'USER';
    DOM.btnLogout.style.display = 'inline-flex';
    DOM.btnCreateTaskOpen.style.display = 'inline-flex';
  }

  // API Call Wrapper
  async function apiCall(endpoint, method = 'GET', body = null) {
    const headers = {
      'Content-Type': 'application/json'
    };

    if (state.token) {
      headers['Authorization'] = `Bearer ${state.token}`;
    }

    const options = { method, headers };
    if (body) {
      options.body = JSON.stringify(body);
    }

    try {
      const response = await fetch(endpoint, options);
      
      if (response.status === 401) {
        showToast('Session expired or invalid login. Please log in.', 'error');
        logout();
        throw new Error('Unauthorized');
      }

      if (response.status === 204) {
        return null;
      }

      const data = await response.json();
      if (!response.ok) {
        throw new Error(data.message || 'API Error');
      }

      return data;
    } catch (err) {
      if (err.message !== 'Unauthorized') {
        showToast(err.message, 'error');
      }
      throw err;
    }
  }

  // Fetch Tasks
  async function fetchTasks() {
    try {
      const tasks = await apiCall('/tasks');
      state.tasks = tasks || [];
      updateStats();
      renderTasks();
    } catch (err) {
      console.error('Failed to fetch tasks:', err);
    }
  }

  // Login Handler
  async function handleLogin(e) {
    e.preventDefault();
    const username = document.getElementById('page-login-username').value;
    const password = document.getElementById('page-login-password').value;

    try {
      const res = await apiCall('/auth/login', 'POST', { username, password });
      state.token = res.token;
      state.username = res.username;
      localStorage.setItem('jwt_token', res.token);
      localStorage.setItem('jwt_username', res.username);
      
      showDashboardView();
      showToast(`Welcome back, ${res.username}! Viewing your tasks.`, 'success');
      fetchTasks();
    } catch (err) {
      // Error toast handled by apiCall
    }
  }

  // Logout Handler
  function logout() {
    state.token = null;
    state.username = null;
    state.tasks = [];
    localStorage.removeItem('jwt_token');
    localStorage.removeItem('jwt_username');
    showLoginView();
    showToast('Logged out successfully.', 'success');
  }

  // Create Task Handler
  async function handleCreateTask(e) {
    e.preventDefault();
    const payload = {
      title: document.getElementById('create-title').value,
      description: document.getElementById('create-description').value,
      due_date: document.getElementById('create-duedate').value || null,
      status: document.getElementById('create-status').value
    };

    try {
      await apiCall('/tasks', 'POST', payload);
      closeModal(DOM.modalCreate);
      DOM.formCreate.reset();
      showToast('Task created successfully!', 'success');
      fetchTasks();
    } catch (err) {}
  }

  // Edit Task Handler
  async function handleEditTask(e) {
    e.preventDefault();
    const id = document.getElementById('edit-id').value;
    const payload = {
      title: document.getElementById('edit-title').value,
      description: document.getElementById('edit-description').value,
      due_date: document.getElementById('edit-duedate').value || null,
      status: document.getElementById('edit-status').value
    };

    try {
      await apiCall(`/tasks/${id}`, 'PUT', payload);
      closeModal(DOM.modalEdit);
      showToast('Task updated successfully!', 'success');
      fetchTasks();
    } catch (err) {}
  }

  // Delete Task Handler
  async function handleDeleteTask() {
    if (!state.deleteTaskId) return;

    try {
      await apiCall(`/tasks/${state.deleteTaskId}`, 'DELETE');
      closeModal(DOM.modalDelete);
      state.deleteTaskId = null;
      showToast('Task deleted successfully!', 'success');
      fetchTasks();
    } catch (err) {}
  }

  // Mark Task Complete
  async function markComplete(id) {
    try {
      await apiCall(`/tasks/${id}/complete`, 'PATCH');
      showToast('Task marked as completed!', 'success');
      fetchTasks();
    } catch (err) {}
  }

  // Render Tasks Grid
  function renderTasks() {
    let filtered = state.tasks.filter(task => {
      const matchesFilter = state.activeFilter === 'all' || task.status === state.activeFilter;
      const matchesSearch = !state.searchQuery || 
        task.title.toLowerCase().includes(state.searchQuery) ||
        (task.description && task.description.toLowerCase().includes(state.searchQuery));
      return matchesFilter && matchesSearch;
    });

    if (filtered.length === 0) {
      DOM.tasksGrid.innerHTML = `
        <div class="empty-state">
          <div class="empty-icon">📭</div>
          <h3>No tasks found for ${escapeHtml(state.username)}</h3>
          <p>Create a new task or adjust your search filters.</p>
        </div>
      `;
      return;
    }

    DOM.tasksGrid.innerHTML = filtered.map(task => `
      <div class="task-card">
        <div>
          <div class="task-card-header">
            <h4 class="task-title">${escapeHtml(task.title)}</h4>
            <span class="status-badge ${task.status}">${formatStatus(task.status)}</span>
          </div>
          <p class="task-description">${escapeHtml(task.description || 'No description provided.')}</p>
        </div>

        <div>
          <div class="task-meta">
            <div class="task-meta-item">
              👤 <span>Owner: <strong>${escapeHtml(task.owner || 'system')}</strong></span>
            </div>
            <div class="task-meta-item">
              📅 <span>${task.due_date ? formatDate(task.due_date) : 'No due date'}</span>
            </div>
          </div>

          <div class="task-actions">
            ${task.status !== 'completed' ? `
              <button class="btn btn-secondary btn-sm btn-complete" data-id="${task.id}">✅ Complete</button>
            ` : '<span></span>'}
            
            <div style="display: flex; gap: 0.4rem;">
              <button class="btn btn-secondary btn-sm btn-edit" data-id="${task.id}">✏️ Edit</button>
              <button class="btn btn-danger btn-sm btn-delete" data-id="${task.id}" data-title="${escapeHtml(task.title)}">🗑️</button>
            </div>
          </div>
        </div>
      </div>
    `).join('');

    // Attach card event handlers
    document.querySelectorAll('.btn-complete').forEach(btn => {
      btn.addEventListener('click', (e) => markComplete(e.target.dataset.id));
    });

    document.querySelectorAll('.btn-edit').forEach(btn => {
      btn.addEventListener('click', (e) => {
        const id = parseInt(e.target.dataset.id);
        const task = state.tasks.find(t => t.id === id);
        if (task) openEditModal(task);
      });
    });

    document.querySelectorAll('.btn-delete').forEach(btn => {
      btn.addEventListener('click', (e) => {
        const id = parseInt(e.target.dataset.id);
        const title = e.target.dataset.title;
        state.deleteTaskId = id;
        document.getElementById('delete-task-title').textContent = title;
        openModal(DOM.modalDelete);
      });
    });
  }

  // Update Stats Header Counter
  function updateStats() {
    const total = state.tasks.length;
    const pending = state.tasks.filter(t => t.status === 'pending').length;
    const progress = state.tasks.filter(t => t.status === 'in_progress').length;
    const completed = state.tasks.filter(t => t.status === 'completed').length;

    DOM.statTotal.textContent = total;
    DOM.statPending.textContent = pending;
    DOM.statProgress.textContent = progress;
    DOM.statCompleted.textContent = completed;
  }

  // Open Edit Modal with Data
  function openEditModal(task) {
    document.getElementById('edit-id').value = task.id;
    document.getElementById('edit-title').value = task.title;
    document.getElementById('edit-description').value = task.description || '';
    document.getElementById('edit-duedate').value = task.due_date || '';
    document.getElementById('edit-status').value = task.status;
    openModal(DOM.modalEdit);
  }

  // Modal Helpers
  function openModal(modal) {
    modal.classList.add('active');
  }

  function closeModal(modal) {
    modal.classList.remove('active');
  }

  // Toast Notification Manager
  function showToast(message, type = 'success') {
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.innerHTML = `
      <span>${type === 'success' ? '✅' : '⚠️'}</span>
      <span>${escapeHtml(message)}</span>
    `;
    DOM.toastContainer.appendChild(toast);

    setTimeout(() => {
      toast.remove();
    }, 4000);
  }

  // Utility Helpers
  function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/[&<>"']/g, match => {
      const map = { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#039;' };
      return map[match];
    });
  }

  function formatStatus(status) {
    const map = {
      'pending': 'Pending',
      'in_progress': 'In Progress',
      'completed': 'Completed'
    };
    return map[status] || status;
  }

  function formatDate(dateStr) {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    return date.toLocaleDateString(undefined, { year: 'numeric', month: 'short', day: 'numeric' });
  }
});
