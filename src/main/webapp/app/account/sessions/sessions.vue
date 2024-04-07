<template>
  <div>
    <h2 v-if="authenticated">
      <span
        >Aktive Sitzungen für [<strong>{{ username }}</strong
        >]</span
      >
    </h2>

    <div class="alert alert-success" v-if="success"><strong>Sitzung wurde geschlossen!</strong></div>
    <div class="alert alert-danger" v-if="error">
      <strong>Es ist ein Fehler aufgetreten!</strong> Die Sitzung konnte nicht geschlossen werden.
    </div>

    <div class="table-responsive">
      <table class="table table-striped">
        <thead>
          <tr>
            <th>IP Adresse</th>
            <th>User Agent</th>
            <th>Datum</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="session in sessions" :key="session.ipAddress">
            <td>{{ session.ipAddress }}</td>
            <td>{{ session.userAgent }}</td>
            <td>{{ session.tokenDate }}</td>
            <td>
              <button type="submit" class="btn btn-primary" v-on:click="invalidate(session.series)">Schließen</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script lang="ts" src="./sessions.component.ts"></script>
