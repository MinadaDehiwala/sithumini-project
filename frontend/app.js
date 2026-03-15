const API_BASE = window.AURABLOOM_API_BASE || "/api";
const STORAGE_KEY = "aurabloom-session";

const state = {
    route: "dashboard",
    session: loadSession(),
    message: null,
    error: null,
    data: {},
};

function loadSession() {
    try {
        return JSON.parse(localStorage.getItem(STORAGE_KEY) || "null");
    } catch {
        return null;
    }
}

function saveSession(session) {
    state.session = session;
    if (session) {
        localStorage.setItem(STORAGE_KEY, JSON.stringify(session));
    } else {
        localStorage.removeItem(STORAGE_KEY);
    }
}

function setStatus(message, isError = false) {
    state.message = isError ? null : message;
    state.error = isError ? message : null;
}

async function api(path, options = {}) {
    const headers = {
        "Content-Type": "application/json",
        ...(options.headers || {}),
    };

    if (state.session?.accessToken) {
        headers.Authorization = `Bearer ${state.session.accessToken}`;
    }

    const response = await fetch(`${API_BASE}${path}`, {
        ...options,
        headers,
    });

    const payload = await response.json().catch(() => ({}));
    if (!response.ok) {
        throw new Error(payload.message || "Request failed");
    }
    return payload.data;
}

function currentUser() {
    return state.session?.user || null;
}

function navigate(route) {
    state.route = route;
    location.hash = route;
}

function sectionTitle(route) {
    return {
        dashboard: ["Clarity at a glance", "Track risk, momentum, and your next steadying move."],
        moods: ["Mood tracking", "Log how you feel and watch emotional patterns take shape."],
        journals: ["Journal reflections", "Capture daily thoughts with simple sentiment analysis and keywords."],
        meditations: ["Meditation rhythm", "Keep a lightweight record of calming sessions and streaks."],
        challenges: ["Daily challenge", "Complete one restorative task and let the streak build."],
        capsules: ["Time capsules", "Write something kind for your future self and unlock it later."],
        community: ["Anonymous community", "Share support without giving up privacy."],
        profile: ["Profile", "Update your account, view badges, and keep your setup current."],
        admin: ["Admin console", "Review flags, community reports, recommendations, and users."],
    }[route];
}

function renderShell() {
    const nav = document.getElementById("primary-nav");
    nav.querySelectorAll("a").forEach((link) => {
        link.classList.toggle("active", link.dataset.route === state.route);
    });

    const sessionPill = document.getElementById("session-pill");
    const logoutButton = document.getElementById("logout-button");
    const adminLink = document.querySelector(".admin-only");
    const user = currentUser();

    if (user) {
        sessionPill.classList.remove("hidden");
        logoutButton.classList.remove("hidden");
        sessionPill.textContent = `${user.fullName} · Level ${user.level}`;
    } else {
        sessionPill.classList.add("hidden");
        logoutButton.classList.add("hidden");
        sessionPill.textContent = "";
    }

    if (user?.role === "ADMIN") {
        adminLink.classList.remove("hidden");
    } else {
        adminLink.classList.add("hidden");
        if (state.route === "admin") {
            navigate("dashboard");
        }
    }
}

function renderApp() {
    renderShell();
    const app = document.getElementById("app");

    if (!currentUser()) {
        app.innerHTML = renderAuth();
        attachGlobalHandlers();
        return;
    }

    const [title, subtitle] = sectionTitle(state.route);
    const content = renderRouteContent();
    app.innerHTML = `
        <section class="hero-panel">
            <div class="hero-copy">
                <span class="eyebrow">AuraBloom</span>
                <h1>${title}</h1>
                <p>${subtitle}</p>
                ${renderStatus()}
            </div>
            <div class="stats-grid">
                <article class="metric-card">
                    <span>Current level</span>
                    <strong>${currentUser().level}</strong>
                    <span>${currentUser().experiencePoints} XP earned</span>
                </article>
                <article class="metric-card">
                    <span>Role</span>
                    <strong>${currentUser().role}</strong>
                    <span>${currentUser().active ? "Active account" : "Inactive account"}</span>
                </article>
                <article class="metric-card">
                    <span>Badges</span>
                    <strong>${currentUser().badges?.length || 0}</strong>
                    <span>Recognition unlocked so far</span>
                </article>
            </div>
        </section>
        ${content}
    `;

    attachGlobalHandlers();
}

function renderStatus() {
    if (state.error) {
        return `<div class="status-message error">${escapeHtml(state.error)}</div>`;
    }
    if (state.message) {
        return `<div class="status-message">${escapeHtml(state.message)}</div>`;
    }
    return "";
}

function renderAuth() {
    return `
        <section class="auth-layout">
            <article class="auth-panel">
                <span class="eyebrow">Welcome back</span>
                <h1>Sign in to your wellness space.</h1>
                <p class="muted">Track moods, journal with insight, complete a daily challenge, and check in with the community.</p>
                ${renderStatus()}
                <form class="form-grid" data-form="login">
                    <div class="field">
                        <label>Email</label>
                        <input name="email" type="email" placeholder="name@example.com" required>
                    </div>
                    <div class="field">
                        <label>Password</label>
                        <input name="password" type="password" placeholder="At least 8 characters" required>
                    </div>
                    <button class="primary-button" type="submit">Sign in</button>
                </form>
            </article>
            <article class="auth-panel">
                <span class="eyebrow">New here</span>
                <h1>Create your AuraBloom account.</h1>
                <p class="muted">The first release is English-only and password reset email is scaffolded but not enabled yet.</p>
                <form class="form-grid" data-form="register">
                    <div class="field">
                        <label>Full name</label>
                        <input name="fullName" type="text" placeholder="Your name" required>
                    </div>
                    <div class="field">
                        <label>Email</label>
                        <input name="email" type="email" placeholder="name@example.com" required>
                    </div>
                    <div class="field">
                        <label>Password</label>
                        <input name="password" type="password" placeholder="At least 8 characters" required>
                    </div>
                    <button class="secondary-button" type="submit">Create account</button>
                </form>
            </article>
        </section>
    `;
}

function renderRouteContent() {
    switch (state.route) {
        case "dashboard":
            return renderDashboard();
        case "moods":
            return renderMoods();
        case "journals":
            return renderJournals();
        case "meditations":
            return renderMeditations();
        case "challenges":
            return renderChallenges();
        case "capsules":
            return renderCapsules();
        case "community":
            return renderCommunity();
        case "profile":
            return renderProfile();
        case "admin":
            return renderAdmin();
        default:
            return renderDashboard();
    }
}

function renderDashboard() {
    const insights = state.data.insights;
    const challenge = state.data.todayChallenge;
    const moodTrend = state.data.weeklyTrend;
    const meditationStats = state.data.meditationStats;

    if (!insights) {
        return document.getElementById("loading-template").innerHTML;
    }

    return `
        <section class="dashboard-grid">
            <article class="panel span-7">
                <div class="panel-header">
                    <div>
                        <span class="eyebrow">Risk snapshot</span>
                        <h2>Emotional insight engine</h2>
                    </div>
                    <span class="metric-pill risk-${insights.riskLevel.toLowerCase()}">${insights.riskLevel} · ${insights.score}/100</span>
                </div>
                <div class="support-banner">
                    <strong>Support guidance</strong>
                    <p>${escapeHtml(insights.supportMessage)}</p>
                </div>
                <div class="panel-grid" style="margin-top: 1rem;">
                    <div class="mini-card">
                        <span>Mood contribution</span>
                        <strong>${insights.breakdown.moodContribution}</strong>
                    </div>
                    <div class="mini-card">
                        <span>Journal contribution</span>
                        <strong>${insights.breakdown.journalContribution}</strong>
                    </div>
                    <div class="mini-card">
                        <span>Meditation contribution</span>
                        <strong>${insights.breakdown.meditationContribution}</strong>
                    </div>
                    <div class="mini-card">
                        <span>Challenge contribution</span>
                        <strong>${insights.breakdown.challengeContribution}</strong>
                    </div>
                </div>
            </article>

            <article class="panel span-5">
                <div class="panel-header">
                    <div>
                        <span class="eyebrow">Recommendations</span>
                        <h2>Three next actions</h2>
                    </div>
                    <span class="metric-pill">${insights.unlockedCapsuleCount} unlocked capsules</span>
                </div>
                <div class="list-stack">
                    ${renderList(insights.recommendations, (item) => `
                        <div class="recommendation">
                            <strong>${escapeHtml(item.title)}</strong>
                            <p>${escapeHtml(item.description)}</p>
                            <span class="muted">${item.category}</span>
                        </div>
                    `, "Recommendations will appear here once the backend has data to work with.")}
                </div>
            </article>

            <article class="panel span-6">
                <div class="panel-header">
                    <div>
                        <span class="eyebrow">Today</span>
                        <h2>Daily challenge</h2>
                    </div>
                    <button class="primary-button" type="button" data-action="complete-challenge">Mark complete</button>
                </div>
                ${challenge ? `
                    <div class="entry">
                        <strong>${escapeHtml(challenge.title)}</strong>
                        <p>${escapeHtml(challenge.description)}</p>
                        <div class="inline-actions">
                            <span class="metric-pill">${challenge.category}</span>
                            <span class="metric-pill">${challenge.rewardXp} XP</span>
                            <span class="metric-pill">${challenge.status}</span>
                        </div>
                    </div>
                ` : '<div class="empty-state">No challenge loaded yet.</div>'}
            </article>

            <article class="panel span-6">
                <div class="panel-header">
                    <div>
                        <span class="eyebrow">Rhythm</span>
                        <h2>Meditation and mood</h2>
                    </div>
                </div>
                <div class="panel-grid">
                    <div class="mini-card">
                        <span>Meditation streak</span>
                        <strong>${meditationStats?.currentStreak ?? 0} days</strong>
                    </div>
                    <div class="mini-card">
                        <span>Total minutes</span>
                        <strong>${meditationStats?.totalMinutes ?? 0}</strong>
                    </div>
                    <div class="mini-card">
                        <span>Weekly moods logged</span>
                        <strong>${sumMoodCounts(moodTrend?.counts || {})}</strong>
                    </div>
                    <div class="mini-card">
                        <span>Weekly mood types</span>
                        <strong>${Object.keys(moodTrend?.counts || {}).length}</strong>
                    </div>
                </div>
            </article>
        </section>
    `;
}

function renderMoods() {
    const moods = state.data.moods || [];
    const weekly = state.data.weeklyTrend;
    const monthly = state.data.monthlyTrend || [];

    return `
        <section class="panel-grid">
            <article class="panel">
                <div class="section-heading">
                    <div>
                        <span class="eyebrow">Log</span>
                        <h2>Capture today's mood</h2>
                    </div>
                </div>
                <form class="form-grid" data-form="mood">
                    <div class="grid-2">
                        <div class="field">
                            <label>Mood</label>
                            <select name="moodType">
                                <option>HAPPY</option>
                                <option>CALM</option>
                                <option>FOCUSED</option>
                                <option>ANXIOUS</option>
                                <option>SAD</option>
                                <option>STRESSED</option>
                            </select>
                        </div>
                        <div class="field">
                            <label>Date</label>
                            <input name="entryDate" type="date">
                        </div>
                    </div>
                    <div class="field">
                        <label>Note</label>
                        <textarea name="note" placeholder="What contributed to this feeling?"></textarea>
                    </div>
                    <button class="primary-button" type="submit">Save mood</button>
                </form>
            </article>

            <article class="panel">
                <div class="section-heading">
                    <div>
                        <span class="eyebrow">Trend</span>
                        <h2>Weekly view</h2>
                    </div>
                </div>
                <div class="panel-grid">
                    ${Object.entries(weekly?.counts || {}).map(([mood, count]) => `
                        <div class="mini-card">
                            <span>${mood}</span>
                            <strong>${count}</strong>
                        </div>
                    `).join("") || '<div class="empty-state">No mood data this week yet.</div>'}
                </div>
                <p class="footer-note">Monthly trend periods loaded: ${monthly.length}</p>
            </article>
        </section>
        <section class="panel" style="margin-top: 1rem;">
            <div class="section-heading">
                <div>
                    <span class="eyebrow">History</span>
                    <h2>Your entries</h2>
                </div>
            </div>
            <div class="list-stack">
                ${renderList(moods, (entry) => `
                    <div class="entry">
                        <strong>${entry.moodType}</strong>
                        <p>${escapeHtml(entry.note || "No note added.")}</p>
                        <span class="muted">${entry.entryDate}</span>
                    </div>
                `, "Start with one honest check-in.")}
            </div>
        </section>
    `;
}

function renderJournals() {
    const journals = state.data.journals || [];
    return `
        <section class="panel-grid">
            <article class="panel">
                <div class="section-heading">
                    <div>
                        <span class="eyebrow">Reflect</span>
                        <h2>Write a journal entry</h2>
                    </div>
                </div>
                <form class="form-grid" data-form="journal">
                    <div class="field">
                        <label>Title</label>
                        <input name="title" type="text" placeholder="A short headline for today" required>
                    </div>
                    <div class="field">
                        <label>Body</label>
                        <textarea name="body" placeholder="What has felt light, heavy, or unresolved?" required></textarea>
                    </div>
                    <div class="field">
                        <label>Date</label>
                        <input name="entryDate" type="date">
                    </div>
                    <button class="primary-button" type="submit">Save reflection</button>
                </form>
            </article>

            <article class="panel">
                <div class="section-heading">
                    <div>
                        <span class="eyebrow">Signal</span>
                        <h2>Recent journal insights</h2>
                    </div>
                </div>
                <div class="list-stack">
                    ${renderList(journals, (entry) => `
                        <div class="entry">
                            <strong>${escapeHtml(entry.title)}</strong>
                            <p>${escapeHtml(entry.body)}</p>
                            <div class="inline-actions">
                                <span class="metric-pill">${entry.sentiment}</span>
                                <span class="metric-pill">Score ${entry.sentimentScore}</span>
                            </div>
                            <p class="muted">Keywords: ${(entry.keywords || []).join(", ") || "None extracted"}</p>
                        </div>
                    `, "Your journal entries and keywords will show up here.")}
                </div>
            </article>
        </section>
    `;
}

function renderMeditations() {
    const sessions = state.data.meditations || [];
    const stats = state.data.meditationStats || {};
    return `
        <section class="panel-grid">
            <article class="panel">
                <div class="section-heading">
                    <div>
                        <span class="eyebrow">Practice</span>
                        <h2>Log a meditation session</h2>
                    </div>
                </div>
                <form class="form-grid" data-form="meditation">
                    <div class="grid-2">
                        <div class="field">
                            <label>Minutes</label>
                            <input name="minutes" type="number" min="1" max="240" value="10" required>
                        </div>
                        <div class="field">
                            <label>Completed at</label>
                            <input name="completedAt" type="datetime-local">
                        </div>
                    </div>
                    <div class="field">
                        <label>Notes</label>
                        <textarea name="notes" placeholder="What did you notice before or after the session?"></textarea>
                    </div>
                    <button class="primary-button" type="submit">Save session</button>
                </form>
            </article>

            <article class="panel">
                <div class="section-heading">
                    <div>
                        <span class="eyebrow">Stats</span>
                        <h2>Steadying rhythm</h2>
                    </div>
                </div>
                <div class="panel-grid">
                    <div class="mini-card"><span>Total sessions</span><strong>${stats.totalSessions || 0}</strong></div>
                    <div class="mini-card"><span>Total minutes</span><strong>${stats.totalMinutes || 0}</strong></div>
                    <div class="mini-card"><span>Current streak</span><strong>${stats.currentStreak || 0}</strong></div>
                </div>
                <div class="list-stack" style="margin-top: 1rem;">
                    ${renderList(sessions, (entry) => `
                        <div class="entry">
                            <strong>${entry.minutes} minutes</strong>
                            <p>${escapeHtml(entry.notes || "No note recorded.")}</p>
                            <span class="muted">${formatDateTime(entry.completedAt)}</span>
                        </div>
                    `, "Your meditation history will appear here.")}
                </div>
            </article>
        </section>
    `;
}

function renderChallenges() {
    const today = state.data.todayChallenge;
    const history = state.data.challengeHistory || [];
    return `
        <section class="panel-grid">
            <article class="panel">
                <div class="section-heading">
                    <div>
                        <span class="eyebrow">Focus</span>
                        <h2>Today's challenge</h2>
                    </div>
                    <button class="primary-button" data-action="complete-challenge" type="button">Complete it</button>
                </div>
                ${today ? `
                    <div class="entry">
                        <strong>${escapeHtml(today.title)}</strong>
                        <p>${escapeHtml(today.description)}</p>
                        <div class="inline-actions">
                            <span class="metric-pill">${today.category}</span>
                            <span class="metric-pill">${today.rewardXp} XP</span>
                            <span class="metric-pill">${today.status}</span>
                        </div>
                    </div>
                ` : '<div class="empty-state">Today’s challenge is still loading.</div>'}
            </article>

            <article class="panel">
                <div class="section-heading">
                    <div>
                        <span class="eyebrow">Momentum</span>
                        <h2>Challenge history</h2>
                    </div>
                </div>
                <div class="timeline">
                    ${renderList(history, (entry) => `
                        <div class="history-row">
                            <strong>${escapeHtml(entry.title)}</strong>
                            <p>${escapeHtml(entry.description)}</p>
                            <span class="muted">${entry.assignedDate} · ${entry.status}</span>
                        </div>
                    `, "Complete one challenge to begin your streak.")}
                </div>
            </article>
        </section>
    `;
}

function renderCapsules() {
    const capsules = state.data.capsules || [];
    return `
        <section class="panel-grid">
            <article class="panel">
                <div class="section-heading">
                    <div>
                        <span class="eyebrow">Future self</span>
                        <h2>Create a time capsule</h2>
                    </div>
                </div>
                <form class="form-grid" data-form="capsule">
                    <div class="field">
                        <label>Title</label>
                        <input name="title" type="text" placeholder="A note for later" required>
                    </div>
                    <div class="field">
                        <label>Message</label>
                        <textarea name="message" placeholder="What do you want your future self to hear?" required></textarea>
                    </div>
                    <div class="field">
                        <label>Unlock at</label>
                        <input name="unlockAt" type="datetime-local" required>
                    </div>
                    <button class="primary-button" type="submit">Save capsule</button>
                </form>
            </article>

            <article class="panel">
                <div class="section-heading">
                    <div>
                        <span class="eyebrow">Archive</span>
                        <h2>Your capsules</h2>
                    </div>
                </div>
                <div class="list-stack">
                    ${renderList(capsules, (entry) => `
                        <div class="entry">
                            <strong>${escapeHtml(entry.title)}</strong>
                            <p>${escapeHtml(entry.message || "Still sealed. The message will appear when the unlock time arrives.")}</p>
                            <span class="muted">${formatDateTime(entry.unlockAt)} · ${entry.unlocked ? "Unlocked" : "Locked"}</span>
                        </div>
                    `, "No capsules yet.")}
                </div>
            </article>
        </section>
    `;
}

function renderCommunity() {
    const posts = state.data.communityPosts || [];
    return `
        <section class="panel-grid">
            <article class="panel">
                <div class="section-heading">
                    <div>
                        <span class="eyebrow">Share</span>
                        <h2>Post anonymously</h2>
                    </div>
                </div>
                <form class="form-grid" data-form="community-post">
                    <div class="field">
                        <label>Post</label>
                        <textarea name="body" placeholder="Share what helped, what felt hard, or a gentle note for someone else." required></textarea>
                    </div>
                    <button class="primary-button" type="submit">Publish anonymously</button>
                </form>
            </article>

            <article class="panel">
                <div class="section-heading">
                    <div>
                        <span class="eyebrow">Feed</span>
                        <h2>Supportive check-ins</h2>
                    </div>
                </div>
                <div class="feed-stack">
                    ${renderList(posts, (post) => `
                        <article class="community-post">
                            <div class="panel-header">
                                <div>
                                    <strong>${escapeHtml(post.anonymousAuthor)}</strong>
                                    <p class="muted">${formatDateTime(post.createdAt)}</p>
                                </div>
                                <span class="metric-pill">${post.moderationStatus}</span>
                            </div>
                            <p>${escapeHtml(post.body)}</p>
                            <div class="inline-actions">
                                <button class="ghost-button" type="button" data-action="react" data-id="${post.id}" data-reaction="SUPPORT">Support · ${post.supportCount}</button>
                                <button class="ghost-button" type="button" data-action="react" data-id="${post.id}" data-reaction="RELATE">Relate · ${post.relateCount}</button>
                                <button class="ghost-button" type="button" data-action="report-post" data-id="${post.id}">Report</button>
                            </div>
                            <form class="form-grid" data-form="comment" data-id="${post.id}" style="margin-top: 0.9rem;">
                                <div class="field">
                                    <label>Add a supportive comment</label>
                                    <textarea name="body" placeholder="Keep it kind and steady." required></textarea>
                                </div>
                                <button class="secondary-button" type="submit">Reply</button>
                            </form>
                            <div class="timeline" style="margin-top: 0.9rem;">
                                ${renderList(post.comments, (comment) => `
                                    <div class="mini-card">
                                        <strong>${escapeHtml(comment.anonymousAuthor)}</strong>
                                        <p>${escapeHtml(comment.body)}</p>
                                        <span class="muted">${formatDateTime(comment.createdAt)}</span>
                                    </div>
                                `, "No replies yet.")}
                            </div>
                        </article>
                    `, "The anonymous community feed is empty right now.")}
                </div>
            </article>
        </section>
    `;
}

function renderProfile() {
    const user = currentUser();
    return `
        <section class="panel-grid">
            <article class="panel">
                <div class="section-heading">
                    <div>
                        <span class="eyebrow">Account</span>
                        <h2>Update your profile</h2>
                    </div>
                </div>
                <form class="form-grid" data-form="profile">
                    <div class="field">
                        <label>Full name</label>
                        <input name="fullName" type="text" value="${escapeAttribute(user.fullName)}" required>
                    </div>
                    <div class="field">
                        <label>New password</label>
                        <input name="password" type="password" placeholder="Leave blank to keep current password">
                    </div>
                    <button class="primary-button" type="submit">Save profile</button>
                </form>
            </article>

            <article class="panel">
                <div class="section-heading">
                    <div>
                        <span class="eyebrow">Progress</span>
                        <h2>Badges and streaks</h2>
                    </div>
                </div>
                <div class="badge-row">
                    ${renderList(user.badges, (badge) => `
                        <div class="badge-chip">
                            <strong>${escapeHtml(badge.name)}</strong>
                            <p>${escapeHtml(badge.description)}</p>
                        </div>
                    `, "No badges unlocked yet.")}
                </div>
            </article>
        </section>
    `;
}

function renderAdmin() {
    if (currentUser()?.role !== "ADMIN") {
        return `<section class="panel"><div class="empty-state">Admin access is required.</div></section>`;
    }

    const overview = state.data.adminOverview || {};
    const flags = state.data.adminFlags || [];
    const posts = state.data.adminReportedPosts || [];
    const recs = state.data.adminRecommendations || [];
    const users = state.data.adminUsers || [];

    return `
        <section class="admin-grid">
            <article class="panel span-12">
                <div class="panel-grid">
                    <div class="mini-card"><span>Total users</span><strong>${overview.totalUsers || 0}</strong></div>
                    <div class="mini-card"><span>Active users</span><strong>${overview.activeUsers || 0}</strong></div>
                    <div class="mini-card"><span>Reported posts</span><strong>${overview.reportedPosts || 0}</strong></div>
                    <div class="mini-card"><span>Open risk flags</span><strong>${overview.openRiskFlags || 0}</strong></div>
                    <div class="mini-card"><span>Total posts</span><strong>${overview.totalPosts || 0}</strong></div>
                    <div class="mini-card"><span>Total challenge logs</span><strong>${overview.totalChallenges || 0}</strong></div>
                </div>
            </article>

            <article class="panel span-6">
                <div class="section-heading">
                    <div>
                        <span class="eyebrow">Risk queue</span>
                        <h2>High-risk flags</h2>
                    </div>
                </div>
                <div class="list-stack">
                    ${renderList(flags, (flag) => `
                        <div class="risk-flag">
                            <strong>${escapeHtml(flag.userName)} · ${flag.score}/100</strong>
                            <p>${escapeHtml(flag.summary)}</p>
                            <div class="inline-actions">
                                <span class="metric-pill risk-${flag.riskLevel.toLowerCase()}">${flag.riskLevel}</span>
                                <button class="ghost-button" type="button" data-action="resolve-flag" data-id="${flag.id}">Resolve</button>
                            </div>
                        </div>
                    `, "No open risk flags.")}
                </div>
            </article>

            <article class="panel span-6">
                <div class="section-heading">
                    <div>
                        <span class="eyebrow">Moderation</span>
                        <h2>Reported posts</h2>
                    </div>
                </div>
                <div class="feed-stack">
                    ${renderList(posts, (post) => `
                        <div class="community-post">
                            <strong>${escapeHtml(post.anonymousAuthor)}</strong>
                            <p>${escapeHtml(post.body)}</p>
                            <div class="inline-actions">
                                <span class="metric-pill">${post.reportCount} reports</span>
                                <button class="ghost-button" type="button" data-action="hide-post" data-id="${post.id}">Hide</button>
                                <button class="ghost-button" type="button" data-action="unhide-post" data-id="${post.id}">Restore</button>
                            </div>
                        </div>
                    `, "No reported posts right now.")}
                </div>
            </article>

            <article class="panel span-7">
                <div class="section-heading">
                    <div>
                        <span class="eyebrow">Recommendations</span>
                        <h2>Create support content</h2>
                    </div>
                </div>
                <form class="form-grid" data-form="recommendation">
                    <div class="grid-2">
                        <div class="field">
                            <label>Title</label>
                            <input name="title" type="text" required>
                        </div>
                        <div class="field">
                            <label>Category</label>
                            <select name="category">
                                <option>GROUNDING</option>
                                <option>REFLECTION</option>
                                <option>MEDITATION</option>
                                <option>CHALLENGE</option>
                                <option>CONNECTION</option>
                            </select>
                        </div>
                    </div>
                    <div class="grid-2">
                        <div class="field">
                            <label>Risk target</label>
                            <select name="targetRiskLevel">
                                <option>LOW</option>
                                <option>MODERATE</option>
                                <option>HIGH</option>
                            </select>
                        </div>
                        <div class="field">
                            <label>Status</label>
                            <select name="active">
                                <option value="true">Active</option>
                                <option value="false">Inactive</option>
                            </select>
                        </div>
                    </div>
                    <div class="field">
                        <label>Description</label>
                        <textarea name="description" required></textarea>
                    </div>
                    <button class="primary-button" type="submit">Save recommendation</button>
                </form>
                <div class="list-stack" style="margin-top: 1rem;">
                    ${renderList(recs, (rec) => `
                        <div class="recommendation">
                            <strong>${escapeHtml(rec.title)}</strong>
                            <p>${escapeHtml(rec.description)}</p>
                            <span class="muted">${rec.category} · ${rec.targetRiskLevel} · ${rec.active ? "Active" : "Inactive"}</span>
                        </div>
                    `, "No recommendation templates yet.")}
                </div>
            </article>

            <article class="panel span-5">
                <div class="section-heading">
                    <div>
                        <span class="eyebrow">Users</span>
                        <h2>Accounts</h2>
                    </div>
                </div>
                <div class="list-stack">
                    ${renderList(users, (user) => `
                        <div class="entry">
                            <strong>${escapeHtml(user.fullName)}</strong>
                            <p>${escapeHtml(user.email)}</p>
                            <span class="muted">${user.role} · ${user.active ? "Active" : "Inactive"} · Level ${user.level}</span>
                        </div>
                    `, "No users found.")}
                </div>
            </article>
        </section>
    `;
}

function renderList(items, renderer, emptyText) {
    if (!items || items.length === 0) {
        return `<div class="empty-state">${emptyText}</div>`;
    }
    return items.map(renderer).join("");
}

async function loadRouteData() {
    if (!currentUser()) {
        renderApp();
        return;
    }

    const loaders = {
        dashboard: async () => {
            const [profile, insights, weeklyTrend, meditationStats, todayChallenge] = await Promise.all([
                api("/users/profile"),
                api("/insights/summary"),
                api("/moods/trend/weekly"),
                api("/meditations/stats"),
                api("/challenges/today"),
            ]);
            saveSession({ ...state.session, user: profile });
            state.data = { ...state.data, insights, weeklyTrend, meditationStats, todayChallenge };
        },
        moods: async () => {
            const [moods, weeklyTrend, monthlyTrend] = await Promise.all([
                api("/moods"),
                api("/moods/trend/weekly"),
                api("/moods/trend/monthly"),
            ]);
            state.data = { ...state.data, moods, weeklyTrend, monthlyTrend };
        },
        journals: async () => {
            state.data = { ...state.data, journals: await api("/journals") };
        },
        meditations: async () => {
            const [meditations, meditationStats] = await Promise.all([api("/meditations"), api("/meditations/stats")]);
            state.data = { ...state.data, meditations, meditationStats };
        },
        challenges: async () => {
            const [todayChallenge, challengeHistory] = await Promise.all([api("/challenges/today"), api("/challenges/history")]);
            state.data = { ...state.data, todayChallenge, challengeHistory };
        },
        capsules: async () => {
            state.data = { ...state.data, capsules: await api("/time-capsules") };
        },
        community: async () => {
            state.data = { ...state.data, communityPosts: await api("/community/posts") };
        },
        profile: async () => {
            const profile = await api("/users/profile");
            saveSession({ ...state.session, user: profile });
        },
        admin: async () => {
            if (currentUser()?.role !== "ADMIN") {
                return;
            }
            const [adminOverview, adminFlags, adminReportedPosts, adminRecommendations, adminUsers] = await Promise.all([
                api("/admin/overview"),
                api("/admin/risk-flags"),
                api("/admin/reported-posts"),
                api("/admin/recommendations"),
                api("/users"),
            ]);
            state.data = { ...state.data, adminOverview, adminFlags, adminReportedPosts, adminRecommendations, adminUsers };
        },
    };

    try {
        appLoading();
        await (loaders[state.route] || loaders.dashboard)();
        setStatus(null);
    } catch (error) {
        if (String(error.message).toLowerCase().includes("unauthorized") || String(error.message).toLowerCase().includes("forbidden")) {
            saveSession(null);
            setStatus("Your session expired. Please sign in again.", true);
        } else {
            setStatus(error.message, true);
        }
    }

    renderApp();
}

function appLoading() {
    const app = document.getElementById("app");
    app.innerHTML = document.getElementById("loading-template").innerHTML;
}

async function onLogin(event) {
    event.preventDefault();
    const form = new FormData(event.target);
    try {
        const auth = await api("/auth/login", {
            method: "POST",
            body: JSON.stringify({
                email: form.get("email"),
                password: form.get("password"),
            }),
        });
        saveSession(auth);
        setStatus("Welcome back.");
        navigate("dashboard");
        await loadRouteData();
    } catch (error) {
        setStatus(error.message, true);
        renderApp();
    }
}

async function onRegister(event) {
    event.preventDefault();
    const form = new FormData(event.target);
    try {
        await api("/auth/register", {
            method: "POST",
            body: JSON.stringify({
                fullName: form.get("fullName"),
                email: form.get("email"),
                password: form.get("password"),
            }),
        });
        setStatus("Account created. Sign in to continue.");
    } catch (error) {
        setStatus(error.message, true);
    }
    renderApp();
}

async function handleFormSubmit(event) {
    const formName = event.target.dataset.form;
    if (!formName) {
        return;
    }

    if (formName === "login") return onLogin(event);
    if (formName === "register") return onRegister(event);

    event.preventDefault();
    const form = new FormData(event.target);

    const handlers = {
        mood: () => api("/moods", {
            method: "POST",
            body: JSON.stringify({
                moodType: form.get("moodType"),
                note: form.get("note"),
                entryDate: form.get("entryDate") || null,
            }),
        }),
        journal: () => api("/journals", {
            method: "POST",
            body: JSON.stringify({
                title: form.get("title"),
                body: form.get("body"),
                entryDate: form.get("entryDate") || null,
            }),
        }),
        meditation: () => api("/meditations", {
            method: "POST",
            body: JSON.stringify({
                minutes: Number(form.get("minutes")),
                notes: form.get("notes"),
                completedAt: form.get("completedAt") ? new Date(form.get("completedAt")).toISOString() : null,
            }),
        }),
        capsule: () => api("/time-capsules", {
            method: "POST",
            body: JSON.stringify({
                title: form.get("title"),
                message: form.get("message"),
                unlockAt: new Date(form.get("unlockAt")).toISOString(),
            }),
        }),
        "community-post": () => api("/community/posts", {
            method: "POST",
            body: JSON.stringify({ body: form.get("body") }),
        }),
        comment: () => api(`/community/posts/${event.target.dataset.id}/comments`, {
            method: "POST",
            body: JSON.stringify({ body: form.get("body") }),
        }),
        profile: async () => {
            const updated = await api("/users/profile", {
                method: "PUT",
                body: JSON.stringify({
                    fullName: form.get("fullName"),
                    password: form.get("password") || null,
                }),
            });
            saveSession({ ...state.session, user: updated });
            return updated;
        },
        recommendation: () => api("/admin/recommendations", {
            method: "POST",
            body: JSON.stringify({
                title: form.get("title"),
                description: form.get("description"),
                category: form.get("category"),
                targetRiskLevel: form.get("targetRiskLevel"),
                active: form.get("active") === "true",
            }),
        }),
    };

    try {
        await handlers[formName]?.();
        setStatus("Saved successfully.");
        await loadRouteData();
    } catch (error) {
        setStatus(error.message, true);
        renderApp();
    }
}

async function handleClick(event) {
    const button = event.target.closest("[data-action]");
    if (!button) {
        return;
    }

    try {
        switch (button.dataset.action) {
            case "complete-challenge":
                await api("/challenges/today/complete", { method: "POST" });
                setStatus("Challenge marked complete.");
                break;
            case "react":
                await api(`/community/posts/${button.dataset.id}/reactions`, {
                    method: "POST",
                    body: JSON.stringify({ reactionType: button.dataset.reaction }),
                });
                setStatus("Reaction saved.");
                break;
            case "report-post":
                await api(`/community/posts/${button.dataset.id}/reports`, {
                    method: "POST",
                    body: JSON.stringify({ reason: "Needs moderator review" }),
                });
                setStatus("Post reported.");
                break;
            case "resolve-flag":
                await api(`/admin/risk-flags/${button.dataset.id}/resolve`, { method: "POST" });
                setStatus("Risk flag resolved.");
                break;
            case "hide-post":
                await api(`/admin/posts/${button.dataset.id}/hide`, { method: "POST" });
                setStatus("Post hidden.");
                break;
            case "unhide-post":
                await api(`/admin/posts/${button.dataset.id}/unhide`, { method: "POST" });
                setStatus("Post restored.");
                break;
            default:
                return;
        }
        await loadRouteData();
    } catch (error) {
        setStatus(error.message, true);
        renderApp();
    }
}

function attachGlobalHandlers() {
    document.querySelectorAll("[data-route]").forEach((link) => {
        link.onclick = (event) => {
            event.preventDefault();
            navigate(link.dataset.route);
            loadRouteData();
        };
    });

    document.getElementById("logout-button").onclick = () => {
        saveSession(null);
        state.data = {};
        setStatus("Signed out.");
        navigate("dashboard");
        renderApp();
    };

    document.querySelectorAll("form[data-form]").forEach((form) => {
        form.onsubmit = handleFormSubmit;
    });

    document.querySelectorAll("[data-action]").forEach((button) => {
        button.onclick = handleClick;
    });
}

function escapeHtml(value) {
    return String(value ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

function escapeAttribute(value) {
    return escapeHtml(value);
}

function formatDateTime(value) {
    if (!value) return "No timestamp";
    return new Date(value).toLocaleString();
}

function sumMoodCounts(counts) {
    return Object.values(counts).reduce((sum, value) => sum + Number(value), 0);
}

window.addEventListener("hashchange", () => {
    state.route = location.hash.replace("#", "") || "dashboard";
    loadRouteData();
});

window.addEventListener("DOMContentLoaded", async () => {
    state.route = location.hash.replace("#", "") || "dashboard";
    renderApp();
    await loadRouteData();
});
