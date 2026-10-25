import axios from 'axios'
import {ElMessage} from 'element-plus'

const request = axios.create({
  timeout: 10000,
})

request.interceptors.request.use((config) => config, (error) => Promise.reject(error))

request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.msg || '请求失败')
      return Promise.reject(new Error(res.msg || '请求失败'))
    }
    return res
  },
  (error) => {
    ElMessage.error(error.message || '网络异常')
    return Promise.reject(error)
  },
)

export default request