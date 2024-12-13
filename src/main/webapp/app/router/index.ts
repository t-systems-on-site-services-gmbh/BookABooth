import { createRouter as createVueRouter, createWebHistory } from 'vue-router';

const Home = () => import('@/core/home/home.vue');
const Ausstellerliste = () => import('@/core/ausstellerliste/ausstellerliste.vue');
const Error = () => import('@/core/error/error.vue');
import account from '@/router/account';
import admin from '@/router/admin';
import entities from '@/router/entities';
import pages from '@/router/pages';

export const createRouter = () =>
  createVueRouter({
    history: createWebHistory(),
    routes: [
      {
        path: '/',
        name: 'Home',
        component: Home,
      },
      {
        path: '/ausstellerliste',
        name: 'Ausstellerliste',
        component: Ausstellerliste,
      },
      {
        path: '/forbidden',
        name: 'Forbidden',
        component: Error,
        meta: { error403: true },
      },
      {
        path: '/not-found',
        name: 'NotFound',
        component: Error,
        meta: { error404: true },
      },
      ...account,
      ...admin,
      entities,
      ...pages,
      {
        path: '/uploads/:pathMatch(.*)*',
        beforeEnter: (to, from, next) => {
          // Let the browser handle this request and bypass the Vue router
          window.location.href = to.fullPath;
        },
      },
      {
        path: '/:pathMatch(.*)*',
        redirect: '/not-found',
      },
    ],
    scrollBehavior(to, from, savedPosition) {
      if (to.hash) {
        return { el: to.hash };
      }
    },
  });

const router = createRouter();

router.beforeResolve(async (to, from, next) => {
  if (!to.matched.length) {
    next({ path: '/not-found' });
    return;
  }
  next();
});

export default router;
