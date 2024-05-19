import axios from 'axios'
import router from '@/router'

import { useSharedStore } from '@/store';
const axiosIns = axios.create({
  // You can add your headers here
  // ================================
  // baseURL: 'https://some-domain.com/api/',
  // timeout: 1000,
  // headers: {'X-Custom-Header': 'foobar'}
})

function setLoadingState(loading: boolean) {
  const store = useSharedStore();
  store.setLoading(loading)
}

// ℹ️ Add request interceptor to send the authorization header on each subsequent request after login
axiosIns.interceptors.request.use(config => {
  setLoadingState(true); // Set loading state to true before the request
  // Retrieve token from localStorage
  const token = localStorage.getItem('accessToken')

  // If token is found
  if (token) {
    const date = new Date().getTime()
    const expiration = JSON.parse(localStorage.getItem('expirationTimeMilli'))
    if (expiration != null && date > expiration) {
      localStorage.setItem('sessionExpired', '1');
      // ℹ️ Logout user and redirect to login page
      // Remove "userData" from localStorage
      localStorage.removeItem('userData')

      // Remove "accessToken" from localStorage
      localStorage.removeItem('accessToken')
      localStorage.removeItem('userAbilities')
      localStorage.removeItem('expirationTimeMilli')
      localStorage.removeItem('userList')
      // If 401 response returned from api
      router.push('/login')
      console.log('expired')
      return Promise.reject({ messsage: 'Token expired', config });
    }
    // Get request headers and if headers is undefined assign blank object
    config.headers = config.headers || {}

    // Set authorization header
    // ℹ️ JSON.parse will convert token to string
    config.headers.Authorization = token ? `Bearer ${JSON.parse(token)}` : ''
  }

  // Return modified config
  return config
},
  (error) => {
    setLoadingState(false); // Set loading state to false if there's an error
    return Promise.reject(error);
  })

// ℹ️ Add response interceptor to handle 401 response
axiosIns.interceptors.response.use(response => {
  setLoadingState(false); // Set loading state to false when the response is received
  return response
}, error => {
  setLoadingState(false); // Set loading state to false if there's an error
  // Handle error
  if (error.messsage === 'Token expired') {
    localStorage.removeItem('userData')
    localStorage.removeItem('adminName')
    // Remove "accessToken" from localStorage
    localStorage.removeItem('accessToken')
    localStorage.removeItem('userAbilities')
    localStorage.removeItem('userList')
    router.push('/login')
    return Promise.reject(error)
  }
  if (error.response.status === 401) {
    // ℹ️ Logout user and redirect to login page
    // Remove "userData" from localStorage
    localStorage.removeItem('userData')
    localStorage.removeItem('adminName')
    // Remove "accessToken" from localStorage
    localStorage.removeItem('accessToken')
    localStorage.removeItem('userAbilities')
    localStorage.removeItem('userList')
    // If 401 response returned from api
    router.push('/login')
  }
  else {
    return Promise.reject(error)
  }
})

export default axiosIns
