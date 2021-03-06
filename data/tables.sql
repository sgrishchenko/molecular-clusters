CREATE DATABASE molec_clust;
CREATE TABLE experiment
(
  id        BIGINT PRIMARY KEY AUTO_INCREMENT,
  date      DATETIME    NOT NULL,
  mov_label VARCHAR(20) NOT NULL,
  X         DOUBLE      NOT NULL,
  Y         DOUBLE      NOT NULL,
  Z         DOUBLE      NOT NULL,
  VX        DOUBLE      NOT NULL,
  VY        DOUBLE      NOT NULL,
  VZ        DOUBLE      NOT NULL
);

CREATE TABLE iteration
(
  id        BIGINT PRIMARY KEY AUTO_INCREMENT,
  frm       DOUBLE     NOT NULL,
  too       DOUBLE     NOT NULL,
  step      DOUBLE     NOT NULL,
  dimension VARCHAR(2) NOT NULL
);

CREATE TABLE link_experiment_iteration
(
  experiment_id BIGINT,
  iteration_id  BIGINT,
  PRIMARY KEY (experiment_id, iteration_id),
  FOREIGN KEY (experiment_id) REFERENCES experiment (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  FOREIGN KEY (iteration_id) REFERENCES iteration (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);


CREATE TABLE trajectory_list
(
  id                         BIGINT PRIMARY KEY AUTO_INCREMENT,
  name                       VARCHAR(100) NOT NULL,
  experiment_id              BIGINT,
  json                       LONGTEXT     NOT NULL,
  radius                     DOUBLE,
  fi                         DOUBLE,
  teta                       DOUBLE,
  path_length                DOUBLE,
  path_length_to_tube_length DOUBLE,
  avg_speed                  DOUBLE,
  avg_free_path              DOUBLE,
  diffusion_coeff            DOUBLE,
  final_fi                   DOUBLE,
  final_teta                 DOUBLE,
  FOREIGN KEY (experiment_id) REFERENCES experiment (id)
    ON DELETE SET NULL
    ON UPDATE CASCADE
);
CREATE UNIQUE INDEX trajectory_list_name_uindex ON trajectory_list (name);