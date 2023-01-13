{ nixpkgs ? import <nixpkgs> {} }:
with nixpkgs;
let
  jdk = openjdk11;
in
mkShell {

  buildInputs = [
    jdk
  ];

  JAVA_HOME="${jdk}/lib/openjdk";
}
