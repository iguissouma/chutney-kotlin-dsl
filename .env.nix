{ nixpkgs ? import <nixpkgs> {} }:
with nixpkgs;
let
  jdk = openjdk11;
  mvn = maven.override { jdk  = jdk; };
in
mkShell {

  buildInputs = [
    jdk
    mvn
  ];

  JAVA_HOME="${jdk}/lib/openjdk";
  M2_HOME="${mvn}/maven";
}
