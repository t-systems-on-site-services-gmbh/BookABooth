<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2 id="bookaboothApp.system.home.createOrEditLabel" data-cy="SystemCreateUpdateHeading">System erstellen oder bearbeiten</h2>
        <div>
          <div class="form-group" v-if="system.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="system.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="system-enabled">Enabled</label>
            <input
              type="checkbox"
              class="form-check"
              name="enabled"
              id="system-enabled"
              data-cy="enabled"
              :class="{ valid: !v$.enabled.$invalid, invalid: v$.enabled.$invalid }"
              v-model="v$.enabled.$model"
            />
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
<script lang="ts" src="./system-update.component.ts"></script>
