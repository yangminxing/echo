import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import './style.css'

import 'element-plus/dist/index.css'
import 'epic-designer/dist/style.css'
import { setupElementPlus } from '@epic-designer/element-plus'
import ElementPlus from 'element-plus'

setupElementPlus()

const app = createApp(App)
app.use(router)
app.use(ElementPlus)
app.mount('#app')