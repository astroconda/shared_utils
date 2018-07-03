// Shared functionality for managing conda installation.

def install(version="4.3.31", py_maj_version=3, install_dir="miniconda") {

    def CONDA_BASE_URL = "https://repo.continuum.io/miniconda"
    def conda_installers  = ["Linux-py2":"Miniconda2-4.5.4-Linux-x86_64.sh",
                             "Linux-py3":"Miniconda3-4.5.4-Linux-x86_64.sh",
                             "MacOSX-py2":"Miniconda2-4.5.4-MacOSX-x86_64.sh",
                             "MacOSX-py3":"Miniconda3-4.5.4-MacOSX-x86_64.sh"]
    def OSname = null
    def uname = sh(script: "uname", returnStdout: true).trim()
    if (uname == "Darwin") {
        OSname = "MacOSX"
        println("OSname=${OSname}")
    }
    if (uname == "Linux") {
        OSname = uname
        println("OSname=${OSname}")
    }
    assert uname != null

    // Check for the availability of a download tool and then use it
    // to get the conda installer.
    def dl_cmds = ["curl -OSs",
                   "wget --no-verbose --server-response --no-check-certificate"]
    def dl_cmd = null
    def stat1 = 999
    for (cmd in dl_cmds) {
        stat1 = sh(script: "which ${cmd.tokenize()[0]}", returnStatus: true)
        if( stat1 == 0 ) {
            dl_cmd = cmd
            break
        }
    }
    if (stat1 != 0) {
        println("Could not find a download tool. Unable to proceed.")
        return false
    }

    def WORKDIR = pwd()
    def conda_install_dir = "${WORKDIR}/${install_dir}"
    def conda_installer =
        conda_installers["${OSname}-py${py_maj_version}"]
    dl_cmd = dl_cmd + " ${CONDA_BASE_URL}/${conda_installer}"
    sh dl_cmd

    // Install specific version of miniconda
    sh "bash ./${conda_installer} -b -p ${conda_install_dir}"
    return true
}
