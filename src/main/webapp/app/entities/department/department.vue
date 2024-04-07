<template>
  <div>
    <h2 id="page-heading" data-cy="DepartmentHeading">
      <span id="department-heading">Departments</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon> <span>Liste aktualisieren</span>
        </button>
        <router-link :to="{ name: 'DepartmentCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-department"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>Department erstellen</span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && departments && departments.length === 0">
      <span>Keine Departments gefunden</span>
    </div>
    <div class="table-responsive" v-if="departments && departments.length > 0">
      <table class="table table-striped" aria-describedby="departments">
        <thead>
          <tr>
            <th scope="row"><span>ID</span></th>
            <th scope="row"><span>Name</span></th>
            <th scope="row"><span>Description</span></th>
            <th scope="row"><span>Company</span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="department in departments" :key="department.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'DepartmentView', params: { departmentId: department.id } }">{{ department.id }}</router-link>
            </td>
            <td>{{ department.name }}</td>
            <td>{{ department.description }}</td>
            <td>
              <span v-for="(company, i) in department.companies" :key="company.id"
                >{{ i > 0 ? ', ' : '' }}
                <router-link class="form-control-static" :to="{ name: 'CompanyView', params: { companyId: company.id } }">{{
                  company.id
                }}</router-link>
              </span>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link
                  :to="{ name: 'DepartmentView', params: { departmentId: department.id } }"
                  class="btn btn-info btn-sm details"
                  data-cy="entityDetailsButton"
                >
                  <font-awesome-icon icon="eye"></font-awesome-icon>
                  <span class="d-none d-md-inline">Details</span>
                </router-link>
                <router-link
                  :to="{ name: 'DepartmentEdit', params: { departmentId: department.id } }"
                  class="btn btn-primary btn-sm edit"
                  data-cy="entityEditButton"
                >
                  <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                  <span class="d-none d-md-inline">Bearbeiten</span>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(department)"
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
        <span id="bookaboothApp.department.delete.question" data-cy="departmentDeleteDialogHeading">Löschen bestätigen</span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-department-heading">Soll Department {{ removeId }} wirklich dauerhaft gelöscht werden?</p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-on:click="closeDialog()">Abbrechen</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-department"
            data-cy="entityConfirmDeleteButton"
            v-on:click="removeDepartment()"
          >
            Löschen
          </button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./department.component.ts"></script>
