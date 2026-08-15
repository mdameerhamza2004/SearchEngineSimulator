const BASE_URL = 'http://localhost:8080/api/analytics';

class AnalyticsClient {
  async request(endpoint, options = {}) {
    const url = `${BASE_URL}${endpoint}`;
    try {
      const response = await fetch(url, {
        ...options,
        headers: {
          'Content-Type': 'application/json',
          ...options.headers,
        },
      });

      if (!response.ok) {
        throw new Error(`Analytics API Error: ${response.status} ${response.statusText}`);
      }

      return await response.json();
    } catch (error) {
      console.error(`Error fetching ${url}:`, error);
      throw error;
    }
  }

  getOverview() {
    return this.request('/overview');
  }

  getTopQueries() {
    return this.request('/top-queries');
  }

  getHistory() {
    return this.request('/history');
  }

  clearHistory() {
    return this.request('/history', { method: 'DELETE' });
  }

  inspectSearch(query) {
    const params = new URLSearchParams({ q: query });
    return this.request(`/inspect?${params.toString()}`);
  }
}

export const analyticsClient = new AnalyticsClient();
