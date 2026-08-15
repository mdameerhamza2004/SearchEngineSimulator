import { apiClient } from './apiClient';

const BASE_URL = 'http://localhost:8080/api/topics';

class LearningClient {
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
        throw new Error(`Learning API Error: ${response.status} ${response.statusText}`);
      }

      return await response.json();
    } catch (error) {
      console.error(`Error fetching ${url}:`, error);
      throw error;
    }
  }

  getTopics() {
    return this.request('');
  }

  getTopic(slug) {
    return this.request(`/${slug}`);
  }
}

export const learningClient = new LearningClient();
