#!/bin/bash

#recursive read_dir
read_dir(){
    for file in `ls -a $1`
    do
        if [ -d $1"/"$file ]
        then
            if [[ $file != '.' && $file != '..' ]]
            then
                read_dir $1"/"$file
            fi
        else
            echo "$1/$file"
        fi
    done
}

# has two args
if [ ! -n "$1" ] || [ ! -n "$2" ] ; then
  echo "$0 <markdown_dir_or_file> <out_dir>"
  exit 1
fi

#get absolute path
abs1=`readlink -f $1`
abs2=`readlink -f $2`

#if $1 is a file, the base_dir is the path before the file name.
#if $1 is a dir, the base_dir is the whole $1 (without tail "/")
base_dir=
#if $1 is a file, the in_files is $1
#if $1 is a dir, the in_files are all of the files in dir $1 (filtered by suffix of ".md")
in_files=
if [ -d $abs1 ]; then
  base_dir=${abs1%/}
  in_files=`read_dir $base_dir|grep -E "\.md"`
elif [ -f $abs1 ]; then
  base_dir=`dirname $abs1`
  in_files=$abs1
else
  echo "arg1 neither a directory nor a regular file"
  exit 1
fi

# output dir
out_dir=${abs2%/}


# handle the in_files
OLD_IFS=$IFS
IFS=$'\n'
for in_file in $in_files
do
  rela_file=
  rela_dir=
  file_name=
  out_file=
  # relative path of the file
  rela_file=${in_file#${base_dir}/}
  # relative path of the file's dir
  rela_dir=`dirname rela_file`
  # file name without suffix
  file_name=`basename $in_file .md`
  #echo $rela_file
  #echo $rela_dir
  #echo $file_name
  mkdir -p $out_dir/$rela_dir
  out_file=`readlink -f $out_dir/$rela_dir/$file_name.html`
  pandoc --mathjax \
    -f markdown \
    -t html \
    -o $out_file \
    $in_file \
  && echo "$in_file => $out_file ... success" \
  || echo "$in_file => $out_file ... fail"
done
IFS=$OLD_IFS
