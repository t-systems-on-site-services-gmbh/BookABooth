<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2 id="bookaboothApp.department.home.createOrEditLabel" data-cy="DepartmentCreateUpdateHeading">
          Department erstellen oder bearbeiten
        </h2>
        <div>
          <div class="form-group" v-if="department.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="department.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="department-name">Name</label>
            <input
              type="text"
              class="form-control"
              name="name"
              id="department-name"
              data-cy="name"
              :class="{ valid: !v$.name.$invalid, invalid: v$.name.$invalid }"
              v-model="v$.name.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="department-description">Description</label>
            <input
              type="text"
              class="form-control"
              name="description"
              id="department-description"
              data-cy="description"
              :class="{ valid: !v$.description.$invalid, invalid: v$.description.$invalid }"
              v-model="v$.description.$model"
            />
          </div>
          <div class="form-group">
            <label for="department-company">Company</label>
            <select
              class="form-control"
              id="department-companies"
              data-cy="company"
              multiple
              name="company"
              v-if="department.companies !== undefined"
              v-model="department.companies"
            >
              <option
                v-bind:value="getSelected(department.companies, companyOption, 'id')"
                v-for="companyOption in companies"
                :key="companyOption.id"
              >
                {{ companyOption.id }}
              </option>
            </select>
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" v-on:click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span>Abbrechen</span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="v$.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span>Speichern</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./department-update.component.ts"></script>
