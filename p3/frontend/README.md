# OptCulture 3.0 Frontend 

The code in based on Vue 3 JS framework, Vuetify component library, Vuexy template, axios library. 

## Project Setup

### Prerequistes: 

- install `npm 18.x`
- install `nvm` if you have other versions of node already installed.


```sh
npm install
```

#### Compile and Hot-Reload for Development

```sh
npm run dev
```

#### Type-Check, Compile and Minify for Production

```sh
npm run build
```

## Recommended IDE Setup

[VSCode](https://code.visualstudio.com/) + [Volar](https://marketplace.visualstudio.com/items?itemName=johnsoncodehk.volar) (and disable Vetur).

### Type Support for `.vue` Imports in TS

Since TypeScript cannot handle type information for `.vue` imports, they are shimmed to be a generic Vue component type by default. In most cases this is fine if you don't really care about component prop types outside of templates.

However, if you wish to get actual prop types in `.vue` imports (for example to get props validation when using manual `h(...)` calls), you can run `Volar: Switch TS Plugin on/off` from VSCode command palette.

## Directory structure and conventions
```
frontend/src
├── assets : images and other non code assets
├── components : components that can be used in multiple pages
├── @core : vendor code - don't change
├── @fake-db : mock apis to work without backend dependencies
├── @iconify : icons. No reason to change
├── layouts : App shell (header, footer, menu)
├── @layouts : App shell (header, footer, menu)
├── navigation/vertical : add any menu entry here
├── pages : top level page shown when navigating
├── plugins : 3rd party plugin configuration
├── router : routing configuration
├── styles : common styles
└── views : components specific to a page / module.
```

## Creating a new `page`

1. Create a new .vue file in the appropriate pages/module. 

* There are some special page names like index / [`path`].  
* Refer the [page creation help for more](https://demos.pixinvent.com/vuexy-vuejs-admin-template/documentation/guide/how-to-create-a-new-page.html#how-to-create-a-new-page)

2. If needed, add a link to the page in the `navigation/vertical/{module}.ts`.
* if you create a new `{module.ts}` then include that module in `navigation/vertical/index.ts`

3. Optionally add to `app-search-bar/index.vue` and `NavBarShortcuts.vue`

4. Page is build with views and components. All views should be under `view/{module}/{Module}ViewName{Type}.vue`

5. If you need to create a mock api, create one in `@fakedb/api/{apiendpoint}.ts` and import the file in `@fakedb/db.ts`

