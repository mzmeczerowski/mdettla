package mdettla.directorytree;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

public class DirectoryTree {

	private final Tree<ShortNameFile> tree;

	public static void main(String[] args) throws FileNotFoundException {
		File root = args.length > 0 ? new File(args[0]) : new File(".");
		if (!root.exists()) {
			throw new FileNotFoundException(root.toString());
		}
		DirectoryTree directoryTree = new DirectoryTree(root);
		System.out.println(directoryTree);
	}

	public DirectoryTree(File root) {
		tree = new Tree<ShortNameFile>(new ShortNameFile(root));
		buildTree(root);
	}

	private void buildTree(File parent) {
		if (parent.isDirectory()) {
			File[] sortedFiles = parent.listFiles();
			Arrays.sort(sortedFiles);
			for (File file : sortedFiles) {
				if (!isIgnored(file)) {
					tree.addLeaf(new ShortNameFile(parent), new ShortNameFile(file));
					buildTree(file);
				}
			}
		}
	}

	private boolean isIgnored(File file) {
		return ".svn".equals(file.getName());
	}

	@Override
	public String toString() {
		return tree.toString();
	}
}
