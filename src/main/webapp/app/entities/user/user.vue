<template>
  <div>
    <h2 id="page-heading" data-cy="UserHeading">
      <span id="user-heading">Benutzer</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon> <span>Liste aktualisieren</span>
        </button>
        <router-link :to="{ name: 'UserCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-user"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>Benutzer erstellen</span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && users && users.length === 0">
      <span>Keine Benutzer gefunden</span>
    </div>
    <div class="table-responsive" v-if="users && users.length > 0">
      <table class="table table-striped" aria-describedby="users">
        <thead>
          <tr>
            <th scope="row"><span>ID</span></th>
            <th scope="row"><span>Login</span></th>
            <th scope="row"><span>Vorname</span></th>
            <th scope="row"><span>Nachname</span></th>
            <th scope="row"><span>Firma</span></th>
            <th scope="row"><span>E-Mail</span></th>
            <th scope="row"><span>Role</span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in users" :key="user.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'UserView', params: { userLogin: user.login } }">{{ user.id }}</router-link>
            </td>
            <td>{{ user.login}}</td>
            <td>{{ user.firstName}}</td>
            <td>{{ user.lastName}}</td>
            <td>{{ user.user.company?.name}}</td>
            <td>{{ user.email}}</td>
            <td>{{ user.authorities?.includes('ROLE_ADMIN') ? 'Administrator' : 'Benutzer' }}</td>
            <td class="text-right">
              <div class="btn-group">
                <router-link
                  :to="{ name: 'UserView', params: { userLogin: user.login } }"
                  class="btn btn-info btn-sm details"
                  data-cy="entityDetailsButton"
                >
                  <font-awesome-icon icon="eye"></font-awesome-icon>
                  <span class="d-none d-md-inline">Details</span>
                </router-link>
                <router-link
                  :to="{ name: 'UserEdit', params: { userLogin: user.login } }"
                  class="btn btn-primary btn-sm edit"
                  data-cy="entityEditButton"
                >
                  <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                  <span class="d-none d-md-inline">Bearbeiten</span>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(user)"
                  variant="danger"
                  class="btn btn-sm"
                  data-cy="entityDeleteButton"
                  v-b-modal.removeEntity
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                  <span class="d-none d-md-inline">Löschen</span>
                </b-button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <b-modal ref="removeEntity" id="removeEntity">
      <template #modal-title>
        <span id="bookaboothApp.user.delete.question" data-cy="userDeleteDialogHeading">Löschen bestätigen</span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-user-heading">Soll Benutzer {{ removeId }} wirklich dauerhaft gelöscht werden?</p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-on:click="closeDialog()">Abbrechen</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-user"
            data-cy="entityConfirmDeleteButton"
            v-on:click="removeUser()"
          >
            Löschen
          </button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./user.component.ts"></script>