
<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2 id="bookaboothApp.user.home.createOrEditLabel" data-cy="UserCreateUpdateHeading">Benutzer erstellen oder bearbeiten</h2>
        <div>
          <div class="form-group" v-if="user.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="user.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="user-login">Login</label>
            <input
              type="text"
              class="form-control"
              name="login"
              id="user-login"
              data-cy="login"
              :class="{ valid: !v$.login.$invalid, invalid: v$.login.$invalid }"
              v-model="v$.login.$model"
            />
            <div v-if="v$.login.$anyDirty && v$.login.$invalid">
              <small class="form-text text-danger" v-for="error of v$.login.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="user-firstName">Vorname</label>
            <input
              type="text"
              class="form-control"
              name="firstName"
              id="company-firstName"
              data-cy="firstName"
              :class="{ valid: !v$.firstName.$invalid, invalid: v$.firstName.$invalid }"
              v-model="v$.firstName.$model"
            />
            <div v-if="v$.firstName.$anyDirty && v$.firstName.$invalid">
              <small class="form-text text-danger" v-for="error of v$.firstName.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
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
<script lang="ts" src="./user-update.component.ts"></script>
