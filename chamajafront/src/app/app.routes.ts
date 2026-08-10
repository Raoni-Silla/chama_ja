import { Component } from '@angular/core';
import { Routes } from '@angular/router';
import { Landingpage } from './screens/landingpage/landingpage';
import { Login } from './screens/login/login';
import { Register } from './screens/Registro/register/register';
import { RegisterTelefone } from './screens/Registro/register-telefone/register-telefone';
import { TipoUsuario } from './screens/Registro/tipo-usuario/tipo-usuario';
import { Home } from './screens/usuario/home-usuario/home';
import { roleGuard } from './core/guards/role-guard';
import { HomePrestador } from './screens/Prestador/home-prestador/home-prestador';
import { PerfilPrestador } from './screens/usuario/perfil-prestador/perfil-prestador';
import { ResultadosProcuraPrestadores } from './screens/usuario/resultados-procura-prestadores/resultados-procura-prestadores';
import { PerfilUsuario } from './screens/usuario/perfil-usuario/perfil-usuario';
import { Localizacao } from './screens/Registro/localizacao/localizacao';

export const routes: Routes = [
    { path: '', component: Landingpage },
    { path: 'login', component: Login },
    { path: 'register', component: Register },
    { path: 'register/telefone', component: RegisterTelefone },
    { path: 'register/tipousuario', component: TipoUsuario },
    { path: 'homepageuser', component: Home, canActivate: [roleGuard], data: { role: 'USUARIO' } },
    { path: 'homepageprestador', component: HomePrestador, canActivate: [roleGuard], data: { role: 'PRESTADOR' } },
    { path: 'resultados-busca', component: ResultadosProcuraPrestadores, canActivate: [roleGuard], data: { role: 'USUARIO' } },
    { path: 'prestador/:id', component: PerfilPrestador, canActivate: [roleGuard], data: { role: 'USUARIO' } },
    { path: "perfil-usuario", component: PerfilUsuario, canActivate: [roleGuard], data: { role: 'USUARIO' } },
    { path: "register/localizacao", component : Localizacao }
];
