const BASE_URL = 'http://localhost:8080/api';

/**
 * Handles API calls to the backend.
 */
class ApiClient {
  async request(endpoint, options = {}) {
    const url = `${BASE_URL}${endpoint}`;
    try {
      const response = await fetch(url, {
        ...options,
        credentials: 'include', // Important for HttpOnly cookies
        headers: {
          'Content-Type': 'application/json',
          ...options.headers,
        },
      });

      if (!response.ok) {
        // Return JSON error response if possible
        let errorData = null;
        try {
          errorData = await response.json();
        } catch (e) {
          errorData = { message: `API Error: ${response.status} ${response.statusText}` };
        }
        throw new Error(errorData.message || 'An error occurred');
      }

      // Some endpoints might return no content (like logout)
      if (response.status === 204) return null;
      
      const contentType = response.headers.get('content-type');
      if (contentType && contentType.includes('application/json')) {
        return await response.json();
      }
      return await response.text();
    } catch (error) {
      // Use debug to avoid polluting console with expected 401 auth-check failures
      console.debug(`[apiClient] ${url}:`, error.message);
      throw error;
    }
  }

  // --- Auth APIs ---
  // All auth methods accept a single object argument for consistency.
  register({ name, email, password }) {
    return this.request('/auth/register', {
      method: 'POST',
      body: JSON.stringify({ name, email, password }),
    });
  }

  login({ email, password }) {
    return this.request('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, password }),
    });
  }

  logout() {
    return this.request('/auth/logout', { method: 'POST' });
  }

  me() {
    return this.request('/auth/me', { method: 'GET' });
  }

  forgotPassword({ email }) {
    return this.request('/auth/forgot-password', {
      method: 'POST',
      body: JSON.stringify({ email }),
    });
  }

  resetPassword({ token, newPassword }) {
    return this.request('/auth/reset-password', {
      method: 'POST',
      body: JSON.stringify({ token, newPassword }),
    });
  }

  // --- Search APIs ---
  search(query, limit = 10) {
    const params = new URLSearchParams({ q: query, limit });
    return this.request(`/search?${params.toString()}`);
  }

  autocomplete(prefix, limit = 10) {
    const params = new URLSearchParams({ prefix, limit });
    return this.request(`/autocomplete?${params.toString()}`);
  }

  spellcheck(query) {
    const params = new URLSearchParams({ q: query });
    return this.request(`/spellcheck?${params.toString()}`);
  }

  // --- Visualization APIs ---
  getTrieVisualization(prefix = '') {
    const params = new URLSearchParams({ prefix });
    return this.request(`/visualization/trie?${params.toString()}`);
  }

  getInvertedIndexVisualization(term) {
    const params = new URLSearchParams({ term });
    return this.request(`/visualization/inverted-index?${params.toString()}`);
  }

  getTfIdfVisualization(query) {
    const params = new URLSearchParams({ q: query });
    return this.request(`/visualization/tfidf?${params.toString()}`);
  }

  getLevenshteinVisualization(source, target) {
    const params = new URLSearchParams({ source, target });
    return this.request(`/visualization/levenshtein?${params.toString()}`);
  }

  getTextPipelineVisualization(text) {
    const params = new URLSearchParams({ text });
    return this.request(`/visualization/text-processing?${params.toString()}`);
  }
  // --- Document APIs ---
  getDocuments() {
    return this.request(`/documents`);
  }

  getDocument(id) {
    return this.request(`/documents/${id}`);
  }

  getDocumentStats(id) {
    return this.request(`/documents/${id}/stats`);
  }
}

export const apiClient = new ApiClient();
