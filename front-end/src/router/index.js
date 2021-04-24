import Vue from "vue";
import VueRouter from "vue-router";


Vue.use(VueRouter);

const routes = [
  {
    path: "/home",
    // name: "Home",
    component: () => import("../components/Home.vue"),
    // redirect: "/about"
  },
  {
    path: "/about",
    
    // name: "About",
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/About.vue"),
      // redirect:"/"
  },

];

const router = new VueRouter({
  // mode: 'history',
  routes,
});

export default router;
